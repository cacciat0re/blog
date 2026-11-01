package cn.arorms.blog.app.mappers

import cn.arorms.blog.app.entities.Picture
import cn.arorms.blog.common.responses.PictureVo

fun Picture.toVo(): PictureVo = PictureVo(
    id = this.id ?: 0,
    filename = this.filename,
    originalFilename = this.originalFilename,
    mimeType = this.mimeType,
    size = this.size,
    url = this.url,
    thumbnailUrl = this.thumbnailUrl,
    alt = this.alt,
    showInGallery = this.showInGallery,
    tags = this.tags.map { it.toVo() },
    createdAt = this.createdAt
)