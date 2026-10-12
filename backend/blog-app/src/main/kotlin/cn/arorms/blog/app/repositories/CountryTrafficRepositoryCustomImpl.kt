package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.QCloudflareTraffic
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * @version 1.2.4 2026-10-12
 * @author Sheng
 */
class CountryTrafficRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : CountryTrafficRepositoryCustom {

    override fun getAggregatedCountryTraffic(timeRange: Int): Map<String, Int> {
        val qTraffic = QCloudflareTraffic.cloudflareTraffic
        val visits = qTraffic.visits.sum()

        val startDate = LocalDate.now(ZoneOffset.UTC).minusDays(timeRange.toLong())

        return queryFactory
            .select(qTraffic.countryCode, visits)
            .from(qTraffic)
            .where(qTraffic.date.goe(startDate))
            .groupBy(qTraffic.countryCode)
            .fetch()
            .associate { it.get(qTraffic.countryCode)!! to it.get(visits)!! }
    }

    override fun getAggregatedDateTraffic(from: LocalDate, to: LocalDate): Map<LocalDate, Int> {
        val qTraffic = QCloudflareTraffic.cloudflareTraffic
        val visits = qTraffic.visits.sum()

        return queryFactory
            .select(qTraffic.date, visits)
            .from(qTraffic)
            .where(qTraffic.date.between(from, to))
            .groupBy(qTraffic.date)
            .fetch()
            .associate { it.get(qTraffic.date)!! to it.get(visits)!! }
    }
}