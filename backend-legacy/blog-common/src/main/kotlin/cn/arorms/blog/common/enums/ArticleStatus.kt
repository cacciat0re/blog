package cn.arorms.blog.common.enums

/**
 * Article publication status
 */
enum class ArticleStatus {
    DRAFT,      // Draft, not published yet
    PUBLISHED,  // Published and visible to public
    ARCHIVED    // Archived, not visible in main list
}