package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.repositories.ArticleRepository
import cn.arorms.blog.app.repositories.CategoryRepository
import cn.arorms.blog.app.repositories.TagRepository
import cn.arorms.blog.app.services.ArticleService
import cn.arorms.blog.app.services.ArticleTranslationService
import cn.arorms.blog.app.entities.Article
import cn.arorms.blog.common.enums.ArticleStatus
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.app.mappers.toSummaryVo
import cn.arorms.blog.app.mappers.toVo
import cn.arorms.blog.common.requests.ArticleQueryRequest
import cn.arorms.blog.common.requests.ArticleUpsertRequest
import cn.arorms.blog.common.responses.ArticleSummaryVo
import cn.arorms.blog.common.responses.ArticleVo
import cn.arorms.framework.common.domain.PageResponse
import cn.arorms.framework.common.exception.ResourceNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Article service implementation
 * @author Sheng
 * @version 1.2.0 2026-09-11
 * @since 2026-07-22
 */
@Service
class ArticleServiceImpl(
    private val articleRepository: ArticleRepository,
    private val articleTranslationService: ArticleTranslationService,
    private val tagRepository: TagRepository,
    private val categoryRepository: CategoryRepository
) : ArticleService {

    override fun getArticlePage(pageable: Pageable, query: ArticleQueryRequest?): PageResponse<ArticleSummaryVo> {
        val request = query ?: ArticleQueryRequest(language = Language.EN, keyword = null, categoryId = null, articleStatus = null)

        val articlePage = articleRepository.findArticlePage(pageable, request)
        val summaryPage = articlePage.map { article ->
            article.toSummaryVo(request.language ?: Language.EN)
        }

        return PageResponse.fromPage(summaryPage)
    }

    // readOnly transaction: the translations map is lazy and Jackson touches
    // it while serializing the response
    @Transactional(readOnly = true)
    override fun getById(id: Long): Article {
        return articleRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Article not found with id: $id") }
    }

    override fun getPublishedArticlePage(pageable: Pageable, query: ArticleQueryRequest?): PageResponse<ArticleSummaryVo> {
        val base = query ?: ArticleQueryRequest(language = Language.EN, keyword = null, categoryId = null, articleStatus = null)
        val request = base.copy(articleStatus = ArticleStatus.PUBLISHED)

        val articlePage = articleRepository.findArticlePage(pageable, request)
        val summaryPage = articlePage.map { article ->
            article.toSummaryVo(request.language ?: Language.EN)
        }

        return PageResponse.fromPage(summaryPage)
    }

    override fun getBySlug(language: Language, slug: String): ArticleVo {
        val article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                ?: throw ResourceNotFoundException("Article not found with slug: $slug")
        return article.toVo(language)
    }

    @Transactional
    override fun create(authorId: String, request: ArticleUpsertRequest): Long {
        if (articleRepository.existsBySlug(request.slug)) {
            throw IllegalArgumentException("Article with slug '${request.slug}' already exists")
        }
        val article = Article(
            slug = request.slug,
            status = request.status,
            category = request.categoryId?.let { categoryRepository.getReferenceById(it) },
            authorId = authorId
        )

        if (request.tagIds.isNotEmpty()) {
            val tags = tagRepository.findAllById(request.tagIds)
            article.tags.addAll(tags)
        }
        articleRepository.save(article)
        upsertTranslations(article.id, request)
        return article.id
    }

    @Transactional
    override fun update(authorId: String, request: ArticleUpsertRequest) {
        val id = request.id ?: throw IllegalArgumentException("Article id must not be null for update")
        val article = articleRepository.findById(id)
                .orElseThrow { ResourceNotFoundException("Article not found with id: $id") }

        if (articleRepository.existsBySlugAndIdNot(request.slug, id)) {
            throw IllegalArgumentException("Article with slug '${request.slug}' already exists")
        }

        article.slug = request.slug
        article.status = request.status
        article.category = request.categoryId?.let { categoryRepository.getReferenceById(it) }
        article.tags.clear()
        if (request.tagIds.isNotEmpty()) {
            article.tags.addAll(tagRepository.findAllById(request.tagIds))
        }
        upsertTranslations(id, request)
    }

    private fun upsertTranslations(articleId: Long, request: ArticleUpsertRequest) {
        request.translations.forEach { translationRequest ->
            articleTranslationService.upsertTranslation(articleId, translationRequest)
        }
    }

    @Transactional
    override fun delete(id: Long) {
        if (!articleRepository.existsById(id)) {
            throw ResourceNotFoundException("Article Not Found with id: $id")
        }
        return articleRepository.deleteById(id)
    }

}