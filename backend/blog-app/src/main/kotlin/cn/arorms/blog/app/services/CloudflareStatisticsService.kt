package cn.arorms.blog.app.services

import java.time.LocalDate

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-09-08
 */
interface CloudflareStatisticsService {
    fun getCountryTrafficMap(timeRange: Int): Map<String, Int>
    fun getDateTraffic(from: LocalDate, to: LocalDate): Map<LocalDate, Int>
    fun syncStatistics(date: LocalDate)
}