package cn.arorms.blog.common.requests

import cn.arorms.blog.common.enums.ArticleStatus

/**
 * Request object for creating/updating an article with multilingual support
 * Full data of article: metadata plus all translations, saved in one call
 * @version 1.2.0 2026-09-22
 * @since 2026-05-02
 */
data class ArticleUpsertRequest(
    val id: Long? = null,
    val slug: String,
    val status: ArticleStatus,
    val categoryId: Long?,
    val tagIds: List<Long> = emptyList(),
    val translations: List<ArticleTranslationUpsertRequest> = emptyList(),
)
