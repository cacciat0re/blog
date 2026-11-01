package cn.arorms.blog.common.responses

/**
 * Response DTO for Category (localized name)
 */
data class CategoryVo (
    val id: Long?,
    val name: String,
    val slug: String,
    val parentId: Long?,
)