package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.CloudflareTraffic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-05-10
 */
@Repository
interface CountryTrafficRepository :
    JpaRepository<CloudflareTraffic, Long>,
    QuerydslPredicateExecutor<CloudflareTraffic>,
    CountryTrafficRepositoryCustom {
    fun deleteByDate(date: LocalDate)
}