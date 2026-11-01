package cn.arorms.blog.app.services

import cn.arorms.blog.common.responses.PictureVo
import cn.arorms.framework.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.multipart.MultipartFile

interface PictureService {

    fun findAll(pageable: Pageable): Page<PictureVo>

    fun findGalleryPictures(pageable: Pageable): Page<PictureVo>

    fun findById(id: Long): PictureVo

    fun upload(
        userPrincipal: UserPrincipal,
        file: MultipartFile,
        alt: String?,
        tagIds: Set<Long>?,
        showInGallery: Boolean
    ): PictureVo

    fun updateMetadata(
        id: Long,
        alt: String?,
        tagIds: Set<Long>?,
        showInGallery: Boolean?
    ): PictureVo

    fun delete(id: Long)
}