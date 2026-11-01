package cn.arorms.blog.common.responses

/**
 * View object for aggregated entity counts shown on the admin dashboard
 * @version 1.2.4 2026-10-13
 * @since 2026-10-13
 */
data class EntityCountVo(
    val totalArticles: Long,
    val publishedArticles: Long,
    val draftArticles: Long,
    val archivedArticles: Long,
    val categories: Long,
    val tags: Long,
    val pictures: Long,
)
