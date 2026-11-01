package cn.arorms.blog.common.responses

import cn.arorms.blog.common.enums.Language

/**
 * Admin view of a single article translation, for the edit page.
 * Only carries the markdown source (originalContent) — the rendered HTML
 * (content) is produced client-side on save and only ever travels in the
 * upsert request; the admin never reads it back.
 * @author cacc
 * @version 1.2.0 2026-09-21
 * @since 2026-09-20
 */
data class ArticleTranslationAdminVo(
    val id: Long?,
    val language: Language,
    val title: String,
    val summary: String?,
    val originalContent: String,
    val isAiTranslated: Boolean?,
)