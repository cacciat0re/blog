package cn.arorms.blog.app.schedules

import cn.arorms.blog.app.services.CloudflareStatisticsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * CloudFlare statistics fetch task
 * @version 1.2.0 2026-09-08
 */
@Component
class CloudflareStatisticsFetch(
    private val cloudflareStatisticsService: CloudflareStatisticsService,
) {
    @Scheduled(cron = "0 30 0 * * ?", zone = "UTC")
    fun syncYesterdayStats() {
        cloudflareStatisticsService.syncStatistics(LocalDate.now(ZoneOffset.UTC).minusDays(1))
    }
}