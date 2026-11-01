package cn.arorms.blog.common.requests

import cn.arorms.blog.common.enums.Language

/**
 * Request object for creating/updating a category
 */
data class CategoryUpsertRequest(
    val names: Map<Language, String>,
    val slug: String,
    val parentId: Long?
)