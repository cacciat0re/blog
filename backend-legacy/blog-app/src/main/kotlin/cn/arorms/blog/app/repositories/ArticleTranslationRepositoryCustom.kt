package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.ArticleTranslation

/**
 * @version 1.2.0 2026-09-18
 * @since 2026-09-18
 */
interface ArticleTranslationRepositoryCustom {
    fun getOriginalTranslation(articleId: Long): ArticleTranslation?
}