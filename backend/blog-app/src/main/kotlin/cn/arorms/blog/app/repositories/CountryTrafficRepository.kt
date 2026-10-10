package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.CountryTraffic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * @version 1.2.0 2026-09-08
 * @since 2026-05-10
 */
@Repository
interface CountryTrafficRepository :
    JpaRepository<CountryTraffic, Long>,
    QuerydslPredicateExecutor<CountryTraffic>,
    CountryTrafficRepositoryCustom {
    fun deleteByDate(date: LocalDate)
}