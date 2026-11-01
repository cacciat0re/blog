package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.common.requests.OutboundRequestQueryRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
interface OutboundRequestRepositoryCustom {
    fun findPage(request: OutboundRequestQueryRequest, pageable: Pageable): Page<OutboundRequest>
}
