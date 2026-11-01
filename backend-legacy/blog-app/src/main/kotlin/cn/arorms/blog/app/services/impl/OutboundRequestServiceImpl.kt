package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.app.mappers.toVo
import cn.arorms.blog.app.repositories.OutboundRequestRepository
import cn.arorms.blog.app.services.CloudflareStatisticsService
import cn.arorms.blog.app.services.OutboundRequestService
import cn.arorms.blog.common.enums.OutboundTarget
import cn.arorms.blog.common.enums.ProcessStatus
import cn.arorms.blog.common.requests.OutboundRequestQueryRequest
import cn.arorms.blog.common.responses.OutboundRequestVo
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.time.LocalDate

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
@Service
class OutboundRequestServiceImpl(
    private val outboundRequestRepository: OutboundRequestRepository,
    private val cloudflareStatisticsService: CloudflareStatisticsService,
) : OutboundRequestService {
    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    override fun findPage(request: OutboundRequestQueryRequest, pageable: Pageable): Page<OutboundRequestVo> {
        return outboundRequestRepository.findPage(request, pageable).map { it.toVo() }
    }

    override fun findById(id: Long): OutboundRequestVo = findEntity(id).toVo()

    override fun retry(id: Long) {
        val request = findEntity(id)
        if (request.status != ProcessStatus.FAILED) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Only FAILED request can be retried, current: ${request.status}")
        }
        if (request.retried) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Request has already been retried")
        }
        if (request.isExpired(Instant.now())) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Request expired at ${request.expiresAt}")
        }
        // Claimed before replaying, so a failed retry leaves the request FAILED and no longer retryable
        if (outboundRequestRepository.markRetried(id, ProcessStatus.FAILED, Instant.now()) == 0) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Request has already been retried")
        }

        val claimed = findEntity(id)
        val succeeded = runCatching { replay(claimed) }
            .onFailure { log.warn("Retry of outbound request {} failed", id, it) }
            .isSuccess
        if (succeeded) {
            claimed.status = ProcessStatus.SUCCEEDED
            outboundRequestRepository.save(claimed)
        }
    }

    private fun findEntity(id: Long): OutboundRequest {
        return outboundRequestRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Outbound request not found with id: $id")
        }
    }

    /**
     * Runs the operation recorded by the request, the payload is the JSON form written at failure time
     * @throws Exception when the operation still fails after its retries
     */
    private fun replay(request: OutboundRequest) {
        when (request.target) {
            OutboundTarget.CLOUDFLARE_SYNC_STATISTICS -> {
                val date = LocalDate.parse(objectMapper.readValue(request.payloadJson, String::class.java))
                cloudflareStatisticsService.retrySyncStatistics(date)
            }
        }
    }
}
