package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.Article
import cn.arorms.blog.common.enums.ArticleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


/**
 * Repository interface for Article entity
 * @version 1.2.6 2026-10-13
 * @since 2026-07-22
 */
@Repository
interface ArticleRepository :
    JpaRepository<Article, Long>,
    ArticleRepositoryCustom {
    fun findBySlugAndStatus(slug: String, status: ArticleStatus): Article?
    fun existsBySlug(slug: String): Boolean
    fun existsBySlugAndIdNot(slug: String, id: Long): Boolean
    fun countByStatus(status: ArticleStatus): Long
}