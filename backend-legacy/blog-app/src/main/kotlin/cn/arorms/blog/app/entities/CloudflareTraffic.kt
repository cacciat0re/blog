package cn.arorms.blog.app.entities

import jakarta.persistence.*
import java.time.LocalDate

/**
 * @version 1.2.4 2026-10-12
 * @author Sheng
 */
@Entity
@Table(name = "statistics_country_traffics")
class CloudflareTraffic(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false, length = 10)
    val countryCode: String,

    val requests: Int,
    val visits: Int
)