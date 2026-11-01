package cn.arorms.blog.common.responses

import java.time.LocalDateTime

data class PictureVo(
    val id: Long,
    val filename: String,
    val originalFilename: String,
    val mimeType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String?,
    val alt: String?,
    val showInGallery: Boolean,
    val tags: List<TagVo>,
    val createdAt: LocalDateTime?
)