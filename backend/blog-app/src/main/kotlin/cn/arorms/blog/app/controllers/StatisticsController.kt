package cn.arorms.blog.app.controllers

import cn.arorms.blog.app.services.CloudflareStatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * REST Controller for Site Statistics
 * @author Sheng
 * @version 1.2.4 2026-10-12
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val cloudflareStatisticsService: CloudflareStatisticsService,
) {
    /**
     * Get Country traffic map, grouped by country code
     * @return Map<String, Int> country code and visit number
     */
    @GetMapping("/country-traffic")
    fun getCountryTraffic(@RequestParam timeRange: Int): ResponseEntity<Map<String, Int>> {
        val trafficMap = cloudflareStatisticsService.getCountryTrafficMap(timeRange)
        return ResponseEntity.ok(trafficMap)
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
