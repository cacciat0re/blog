package cn.arorms.blog.common.requests

import cn.arorms.blog.common.enums.Language

/**
 * @version 1.2.0 2026-09-22
 * @since 2026-05-05
 */
data class ArticleTranslationUpsertRequest(
    val id: Long? = null,
    val language: Language,
    val title: String,
    val originalContent: String,
    /** Rendered HTML; null keeps the existing rendered content (lightweight save) */
    val content: String? = null,
    val summary: String?,
    val isAiTranslated: Boolean? = false,
)