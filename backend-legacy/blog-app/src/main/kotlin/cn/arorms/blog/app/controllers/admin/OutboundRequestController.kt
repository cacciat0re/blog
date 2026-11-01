package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.services.CloudflareStatisticsService
import cn.arorms.blog.app.services.OutboundRequestService
import cn.arorms.blog.common.requests.OutboundRequestQueryRequest
import cn.arorms.blog.common.responses.OutboundRequestVo
import cn.arorms.framework.common.domain.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * @author Sheng
 * @version 1.2.4 2026-10-12
 * @since 2026-09-10
 */
@RestController
@RequestMapping("/api/admin/outbound-request")
class OutboundRequestController (
    private val cloudflareStatisticsService: CloudflareStatisticsService,
    private val outboundRequestService: OutboundRequestService,
) {
    /**
     * Pages outbound requests, newest first
     * @param request optional filters: status, target
     */
    @GetMapping
    fun getOutboundRequests(
        pageable: Pageable,
        request: OutboundRequestQueryRequest,
    ): ResponseEntity<PageResponse<OutboundRequestVo>> {
        val page = outboundRequestService.findPage(request, pageable)
        return ResponseEntity.ok(PageResponse.fromPage(page))
    }

    @GetMapping("/{id}")
    fun getOutboundRequest(@PathVariable id: Long): ResponseEntity<OutboundRequestVo> {
        return ResponseEntity.ok(outboundRequestService.findById(id))
    }

    /**
     * Re-run a FAILED outbound request through the regular retry path.
     * A failure is recorded as a new FAILED outbound request, the original record is left unchanged.
     */
    @PostMapping("/{id}/retry")
    fun retryOutboundRequest(@PathVariable id: Long): ResponseEntity<Void> {
        outboundRequestService.retry(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Re-run the Cloudflare statistics sync for a UTC day, e.g. to backfill a FAILED outbound request.
     * A failed sync is recorded as a FAILED outbound request, not as an HTTP error.
     * @param date UTC date to sync, ISO format (yyyy-MM-dd)
     */
    @PostMapping("/cloudflare/sync")
    fun syncCloudflareStatistics(@RequestParam date: LocalDate): ResponseEntity<Void> {
        cloudflareStatisticsService.syncStatistics(date)
        return ResponseEntity.noContent().build()
    }
}