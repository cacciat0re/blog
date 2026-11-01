package cn.arorms.blog.app.repositories

/**
 * Article repository custom interface
 * @author cacc
 * @version 1.1.2 2026-09-01
 * @since 2026-07-22
 */
import cn.arorms.blog.app.entities.Article
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleQueryRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ArticleRepositoryCustom {
    fun findArticlePage(pageable: Pageable, request: ArticleQueryRequest): Page<Article>
    fun findByCategoryId(categoryId: Long, language: Language, pageable: Pageable): Page<Article>
    fun countByCategoryId(categoryId: Long): Long
}