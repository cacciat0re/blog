package cn.arorms.blog.common.requests

/**
 * Request object for creating/updating a tag
 */
data class TagUpsertRequest(
    val id: Long?,
    val name: String,
    val slug: String
)