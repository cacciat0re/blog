package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.app.entities.QOutboundRequest.outboundRequest
import cn.arorms.blog.common.requests.OutboundRequestQueryRequest
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

/**
 * Results are ordered by id descending (newest first), the sort of [Pageable] is not applied
 * @version 1.2.4 2026-10-12
 * @since 2026-10-12
 */
class OutboundRequestRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : OutboundRequestRepositoryCustom {

    override fun findPage(request: OutboundRequestQueryRequest, pageable: Pageable): Page<OutboundRequest> {
        val builder = BooleanBuilder()

        request.status?.let { builder.and(outboundRequest.status.eq(it)) }
        request.target?.let { builder.and(outboundRequest.target.eq(it)) }

        val content = queryFactory
            .selectFrom(outboundRequest)
            .where(builder)
            .orderBy(outboundRequest.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageableExecutionUtils.getPage(content, pageable) {
            queryFactory
                .select(outboundRequest.count())
                .from(outboundRequest)
                .where(builder)
                .fetchOne() ?: 0L
        }
    }
}
