package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.services.CloudflareStatisticsService
import cn.arorms.blog.app.services.EntityStatisticsService
import cn.arorms.blog.common.responses.EntityCountVo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * Admin statistics controller, used by the dashboard
 * @version 1.2.6 2026-10-13
 * @since 2026-10-13
 */
@RestController
@RequestMapping("/api/admin/statistics")
class StatisticsAdminController(
    private val entityStatisticsService: EntityStatisticsService,
    private val cloudflareStatisticsService: CloudflareStatisticsService,
) {

    /**
     * @return aggregated counts of articles (by status), categories, tags and pictures
     */
    @GetMapping("/entity-counts")
    fun getEntityCounts(): ResponseEntity<EntityCountVo> {
        return ResponseEntity.ok(entityStatisticsService.getEntityCounts())
    }

    /**
     * Get daily visits between from and to (both inclusive)
     */
    @GetMapping("/date-traffic")
    fun getDateTraffic(@RequestParam from: LocalDate, to: LocalDate): ResponseEntity<Map<LocalDate, Int>> {
        val dateTraffic = cloudflareStatisticsService.getDateTraffic(from, to)
        return ResponseEntity.ok(dateTraffic)
    }
}