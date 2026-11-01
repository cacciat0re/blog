package cn.arorms.blog.common.responses

import cn.arorms.blog.common.enums.OutboundTarget
import cn.arorms.blog.common.enums.ProcessStatus
import java.time.Instant

/**
 * View object for an outbound request record
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
data class OutboundRequestVo(
    val id: Long,
    val target: OutboundTarget,
    val status: ProcessStatus,
    val retried: Boolean,
    val payloadJson: String,
    val expiresAt: Instant?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)
