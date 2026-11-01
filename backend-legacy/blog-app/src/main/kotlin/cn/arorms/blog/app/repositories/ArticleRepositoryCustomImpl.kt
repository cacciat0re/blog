package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.Article
import cn.arorms.blog.app.entities.QArticle.article
import cn.arorms.blog.app.entities.QArticleTranslation.articleTranslation
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleQueryRequest
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

/**
 * Article repository custom implementation
 * @author cacc
 * @version 1.1.2 2026-09-01
 * @since 2026-07-22
 */
class ArticleRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory,
) : ArticleRepositoryCustom {

    override fun findArticlePage(pageable: Pageable, request: ArticleQueryRequest): Page<Article> {
        val builder = BooleanBuilder()

        if (request.categoryId != null) {
            builder.and(article.category.id.eq(request.categoryId))
        }

        if (request.articleStatus != null) {
            builder.and(article.status.eq(request.articleStatus))
        }

        if (!request.keyword.isNullOrBlank()) {
            val keywordPattern = "%${request.keyword}%"

            builder.and(
                articleTranslation.title.like(keywordPattern)
                    .or(articleTranslation.summary.like(keywordPattern))
                    .or(articleTranslation.content.like(keywordPattern))
            )
        }

        // left join restricted to the requested language in the ON clause, so
        // articles without any translation still appear (title falls back to "")
        val query = queryFactory.selectFrom(article)
            .leftJoin(article.translations, articleTranslation)
            .on(articleTranslation.language.eq(request.language))
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        query.orderBy(article.createdAt.desc())

        val content = query.fetch()

        val countQuery = queryFactory.select(article.count())
            .from(article)
            .leftJoin(article.translations, articleTranslation)
            .on(articleTranslation.language.eq(request.language))
            .where(builder)

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findByCategoryId(categoryId: Long, language: Language, pageable: Pageable): Page<Article> {
        val builder = BooleanBuilder()
        builder.and(article.category.id.eq(categoryId))

        val query = queryFactory.selectFrom(article)
            .leftJoin(article.translations, articleTranslation)
            .on(articleTranslation.language.eq(language))
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(article.createdAt.desc())

        val content = query.fetch()

        val countQuery = queryFactory.select(article.count())
            .from(article)
            .leftJoin(article.translations, articleTranslation)
            .on(articleTranslation.language.eq(language))
            .where(builder)

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun countByCategoryId(categoryId: Long): Long =
        queryFactory.select(article.count())
            .from(article)
            .where(article.category.id.eq(categoryId))
            .fetchOne() ?: 0L
}