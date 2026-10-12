package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.OutboundRequest
import cn.arorms.blog.common.enums.ProcessStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * @version 1.2.4 2026-10-12
 * @since 2026-09-08
 */
@Repository
interface OutboundRequestRepository :
    JpaRepository<OutboundRequest, Long>,
    OutboundRequestRepositoryCustom {

    /**
     * Atomically marks a FAILED request as retried, so only one caller can retry it
     * @return number of rows updated, 0 when the request is not FAILED or was already retried
     */
    @Transactional
    @Modifying
    @Query("update OutboundRequest r set r.retried = true, r.updatedAt = :now where r.id = :id and r.retried = false and r.status = :status")
    fun markRetried(
        @Param("id") id: Long,
        @Param("status") status: ProcessStatus,
        @Param("now") now: Instant,
    ): Int
}