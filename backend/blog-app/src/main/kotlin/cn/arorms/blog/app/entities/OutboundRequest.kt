package cn.arorms.blog.app.entities

import cn.arorms.blog.common.enums.OutboundTarget
import cn.arorms.blog.common.enums.ProcessStatus
import cn.arorms.framework.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.Instant

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-09-08
 */
@Entity @Table(name = "outbound_requests")
class OutboundRequest(
    // Default backfills rows created before this column, which only ever held the Cloudflare sync
    @Enumerated(EnumType.STRING)
    @Column(name = "target", columnDefinition = "varchar(64) not null default 'CLOUDFLARE_SYNC_STATISTICS'")
    val target: OutboundTarget,

    @Column(name = "payload_json")
    val payloadJson: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: ProcessStatus,

    // Set once by a manual retry, a request can be retried at most once
    @Column(name = "retried", columnDefinition = "boolean not null default false")
    var retried: Boolean = false,

    val expiresAt: Instant? = null,
) : BaseEntity() {

    fun isExpired(now: Instant): Boolean = expiresAt?.isBefore(now) == true
}