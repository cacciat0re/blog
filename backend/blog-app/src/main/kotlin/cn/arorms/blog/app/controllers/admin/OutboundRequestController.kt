package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.services.CloudflareStatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * @author Sheng
 * @version 1.2.0 2026-09-20
 * @since 2026-09-10
 */
@RestController
@RequestMapping("/api/admin/outbound-request")
class OutboundRequestController (private val cloudflareStatisticsService: CloudflareStatisticsService) {
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