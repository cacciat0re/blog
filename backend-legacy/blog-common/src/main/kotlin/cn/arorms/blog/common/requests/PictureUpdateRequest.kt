package cn.arorms.blog.common.requests

data class PictureUpdateRequest(
    val alt: String? = null,
    val tagIds: Set<Long>? = null,
    val showInGallery: Boolean? = null
)
