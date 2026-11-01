package cn.arorms.blog.common.requests

import cn.arorms.blog.common.enums.OutboundTarget
import cn.arorms.blog.common.enums.ProcessStatus

/**
 * Optional filters for querying outbound requests
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
data class OutboundRequestQueryRequest(
    val status: ProcessStatus? = null,
    val target: OutboundTarget? = null,
)