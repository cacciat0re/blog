package cn.arorms.blog.app.repositories

import java.time.LocalDate

/**
 * @version 1.2.4 2026-10-12
 * @author Sheng
 */
interface CountryTrafficRepositoryCustom {
    fun getAggregatedCountryTraffic(timeRange: Int): Map<String, Int>
    fun getAggregatedDateTraffic(from: LocalDate, to: LocalDate): Map<LocalDate, Int>
}