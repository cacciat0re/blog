package cn.arorms.blog.app.services

import cn.arorms.blog.common.requests.OutboundRequestQueryRequest
import cn.arorms.blog.common.responses.OutboundRequestVo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
interface OutboundRequestService {
    fun findPage(request: OutboundRequestQueryRequest, pageable: Pageable): Page<OutboundRequestVo>
    fun findById(id: Long): OutboundRequestVo

    /**
     * Re-runs the recorded operation through the regular resilient path,
     * a failure is handled by the same retry and fallback as the scheduled sync
     */
    fun retry(id: Long)
}
