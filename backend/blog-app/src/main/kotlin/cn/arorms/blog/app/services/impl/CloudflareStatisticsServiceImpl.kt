package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.CloudflareTraffic
import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.app.repositories.CountryTrafficRepository
import cn.arorms.blog.app.repositories.OutboundRequestRepository
import cn.arorms.blog.app.services.CloudflareStatisticsService
import cn.arorms.blog.common.enums.OutboundTarget
import cn.arorms.blog.common.enums.ProcessStatus
import cn.arorms.blog.common.enums.TimeRange
import cn.arorms.blog.common.responses.AdaptiveGroup
import cn.arorms.blog.common.responses.CloudflareViewer
import io.github.resilience4j.retry.RetryRegistry
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-09-08
 */
@Service
class CloudflareStatisticsServiceImpl (
    private val cloudflareClient: HttpGraphQlClient,
    private val countryTrafficRepository: CountryTrafficRepository,
    private val outboundRequestRepository: OutboundRequestRepository,
    private val transactionTemplate: TransactionTemplate,
    private val retryRegistry: RetryRegistry,
    @Value($$"${application.cloudflare.zone-id}") private val zoneId: String
) : CloudflareStatisticsService {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val RETRY_NAME = "fetchCloudflareStatistics"
    }

    /**
     * Get aggregated country traffic map
     */
    override fun getCountryTrafficMap(timeRange: Int): Map<String, Int> {
        val range = TimeRange.fromInt(timeRange)
            ?: throw IllegalArgumentException("timeRange must be 1, 7, or 30")
        return countryTrafficRepository.getAggregatedCountryTraffic(range.days)
    }

    fun getHttpRequestsStatistics(startTime: Instant, endTime: Instant, host: String): List<AdaptiveGroup> {
        val startTimeStr = startTime.toString()
        val endTimeStr = endTime.toString()

        val query = """
            query GetStats(${"$"}zoneTag: String!, ${"$"}start: String!, ${"$"}end: String!, ${"$"}host: String!) {
              viewer {
                zones(filter: { zoneTag: ${"$"}zoneTag }) {
                  httpRequestsAdaptiveGroups(
                    limit: 100,
                    filter: {
                      datetime_geq: ${"$"}start,
                      datetime_leq: ${"$"}end,
                      clientRequestHTTPHost: ${"$"}host
                    }
                  ) {
                    dimensions { clientCountryName }
                    count
                    sum { visits }
                  }
                }
              }
            }
        """.trimIndent()

        return cloudflareClient.document(query)
            .variable("zoneTag", zoneId)
            .variable("start", startTimeStr)
            .variable("end", endTimeStr)
            .variable("host", host)
            .retrieve("viewer")
            .toEntity(CloudflareViewer::class.java)
            .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
            .block(Duration.ofSeconds(90))
            ?.zones?.firstOrNull()?.httpRequestsAdaptiveGroups ?: emptyList()
    }

    fun getHttpRequestsStatisticsByDate(date: LocalDate) {
        val startTime = date.atStartOfDay(ZoneOffset.UTC).toInstant()
        val endTime = startTime.plus(1, ChronoUnit.DAYS)

        val groups = getHttpRequestsStatistics(startTime, endTime, "blog.arorms.cn")

        val entities = groups.map { group ->
            CloudflareTraffic(
                date = startTime.atZone(ZoneOffset.UTC).toLocalDate(),
                countryCode = group.dimensions.clientCountryName ?: "XX",
                requests = group.count,
                visits = group.sum.visits
            )
        }

        // Empty response is skipped rather than deleting the existing rows for the date
        if (entities.isNotEmpty()) {
            transactionTemplate.executeWithoutResult {
                countryTrafficRepository.deleteByDate(date)
                countryTrafficRepository.saveAll(entities)
            }
        }
    }

    @Retry(name = RETRY_NAME, fallbackMethod = "reportSyncFailure")
    override fun syncStatistics(date: LocalDate) = getHttpRequestsStatisticsByDate(date)

    override fun retrySyncStatistics(date: LocalDate) {
        retryRegistry.retry(RETRY_NAME).executeRunnable { getHttpRequestsStatisticsByDate(date) }
    }

    fun reportSyncFailure(date: LocalDate, e: Throwable) {
        log.error("Cloudflare statistics sync failed for {}", date, e)
        val payload = jacksonObjectMapper().writeValueAsString(date.toString())
        outboundRequestRepository.save(
            OutboundRequest(
                target = OutboundTarget.CLOUDFLARE_SYNC_STATISTICS,
                payloadJson = payload,
                status = ProcessStatus.FAILED,
                expiresAt = Instant.now().plus(7, ChronoUnit.DAYS),
            )
        )
    }

    /**
     * Get daily visits between from and to (both inclusive), keyed by date
     */
    override fun getDateTraffic(from: LocalDate, to: LocalDate): Map<LocalDate, Int> {
        require(!from.isAfter(to)) { "from must not be after to" }
        return countryTrafficRepository.getAggregatedDateTraffic(from, to)
    }
}