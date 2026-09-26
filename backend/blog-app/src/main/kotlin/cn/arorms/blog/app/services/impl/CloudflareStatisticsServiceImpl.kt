package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.CountryTraffic
import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.app.repositories.CountryTrafficRepository
import cn.arorms.blog.app.repositories.OutboundRequestRepository
import cn.arorms.blog.app.services.CloudflareStatisticsService
import cn.arorms.blog.common.enums.ProcessStatus
import cn.arorms.blog.common.enums.TimeRange
import cn.arorms.blog.common.responses.AdaptiveGroup
import cn.arorms.blog.common.responses.CloudflareViewer
import cn.arorms.blog.common.responses.CountryTrafficMap
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.stereotype.Service
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * @version 1.2.0 2026-09-08
 * @since 2026-09-08
 */
@Service
class CloudflareStatisticsServiceImpl (
    private val cloudflareClient: HttpGraphQlClient,
    private val countryTrafficRepository: CountryTrafficRepository,
    private val outboundRequestRepository: OutboundRequestRepository,
    @Value($$"${application.cloudflare.zone-id}") private val zoneId: String
) : CloudflareStatisticsService {
    /**
     * Get aggregated country traffic map
     */
    override fun getCountryTrafficMap(timeRange: Int): List<CountryTrafficMap> {
        val range = TimeRange.fromInt(timeRange)
            ?: throw IllegalArgumentException("timeRange must be 1, 7, or 30")
        return countryTrafficRepository.getAggregatedTrafficMap(range.days)
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
            .block()
            ?.zones?.firstOrNull()?.httpRequestsAdaptiveGroups ?: emptyList()
    }

    fun getHttpRequestsStatisticsByDate(date: LocalDate) {
        val startTime = date.atStartOfDay(ZoneOffset.UTC).toInstant()
        val endTime = startTime.plus(1, ChronoUnit.DAYS)

        val groups = getHttpRequestsStatistics(startTime, endTime, "blog.arorms.cn")

        val entities = groups.map { group ->
            CountryTraffic(
                date = startTime.atZone(ZoneOffset.UTC).toLocalDate(),
                countryCode = group.dimensions.clientCountryName ?: "XX",
                requests = group.count,
                visits = group.sum.visits
            )
        }

        if (entities.isNotEmpty()) {
            countryTrafficRepository.saveAll(entities)
        }
    }

    @Retry(name = "fetchCloudflareStatistics", fallbackMethod = "reportSyncFailure")
    override fun syncStatistics(date: LocalDate) = getHttpRequestsStatisticsByDate(date)

    fun reportSyncFailure(date: LocalDate, e: Throwable) {
        val payload = jacksonObjectMapper().writeValueAsString(date.toString())
        outboundRequestRepository.save(
            OutboundRequest(
                payloadJson = payload,
                status = ProcessStatus.FAILED,
                expiresAt = Instant.now().plus(7, ChronoUnit.DAYS),
            )
        )
    }
}