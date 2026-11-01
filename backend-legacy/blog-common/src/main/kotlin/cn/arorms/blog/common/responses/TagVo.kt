package cn.arorms.blog.common.responses

/**
 * Data Transfer Object for Tag (localized name)
 */
data class TagVo(
    val id: Long?,
    val name: String,
    val slug: String
)
