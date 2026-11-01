package cn.arorms.blog.common.responses

/**
 * Localized category tree node
 */
data class CategoryTreeNode(
    val id: Long,
    val name: String,
    val slug: String,
    val children: List<CategoryTreeNode>
)