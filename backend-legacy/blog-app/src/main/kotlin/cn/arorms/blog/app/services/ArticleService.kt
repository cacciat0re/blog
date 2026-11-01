package cn.arorms.blog.app.services

/**
 * Article service interface
 * @author Sheng
 * @version 1.2.0 2026-09-11
 * @since 2026-07-22
 */
import cn.arorms.blog.app.entities.Article
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleQueryRequest
import cn.arorms.blog.common.requests.ArticleUpsertRequest
import cn.arorms.blog.common.responses.ArticleSummaryVo
import cn.arorms.blog.common.responses.ArticleVo
import cn.arorms.framework.common.domain.PageResponse
import org.springframework.data.domain.Pageable

interface ArticleService {

    fun getArticlePage(pageable: Pageable, query: ArticleQueryRequest?): PageResponse<ArticleSummaryVo>

    fun getPublishedArticlePage(pageable: Pageable, query: ArticleQueryRequest?): PageResponse<ArticleSummaryVo>

    fun getBySlug(language: Language, slug: String): ArticleVo

    fun getById(id: Long): Article

    /**
     * Create article (metadata + translations in one transaction),
     * returns the id of the new article
     */
    fun create(authorId: String, request: ArticleUpsertRequest): Long

    /**
     * Update article metadata and upsert its translations in one transaction
     */
    fun update(authorId: String, request: ArticleUpsertRequest)

    fun delete(id: Long)
}