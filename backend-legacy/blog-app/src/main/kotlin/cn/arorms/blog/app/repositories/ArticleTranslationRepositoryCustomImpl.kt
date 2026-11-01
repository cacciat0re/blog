package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.ArticleTranslation
import cn.arorms.blog.app.entities.QArticleTranslation.articleTranslation
import com.querydsl.jpa.impl.JPAQueryFactory

/**
 * @author Sheng
 * @version 1.2.0 2026-09-18
 * @since 2026-09-18
 */
class ArticleTranslationRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : ArticleTranslationRepositoryCustom {
    override fun getOriginalTranslation(articleId: Long): ArticleTranslation? =
        queryFactory.selectFrom(articleTranslation)
            .where(
                articleTranslation.article.id.eq(articleId),
                articleTranslation.isAiTranslated.isFalse
            )
            .orderBy(articleTranslation.id.asc())
            .fetchFirst()
}