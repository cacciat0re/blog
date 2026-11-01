package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.Picture
import cn.arorms.blog.app.entities.Tag
import cn.arorms.blog.app.mappers.toVo
import cn.arorms.blog.app.properties.S3Properties
import cn.arorms.blog.app.repositories.PictureRepository
import cn.arorms.blog.app.repositories.TagRepository
import cn.arorms.blog.app.services.PictureService
import cn.arorms.blog.common.responses.PictureVo
import cn.arorms.framework.common.exception.ResourceNotFoundException
import cn.arorms.framework.security.UserPrincipal
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * @version 1.1.0 2026-08-30
 * @since 2026-08-30
 * @author cacc
 */
@Service
class PictureServiceImpl(
    private val pictureRepository: PictureRepository,
    private val tagRepository: TagRepository,
    private val s3Client: S3Client,
    private val s3Properties: S3Properties,
    @Value("\${app.thumbnail.width:512}") private val thumbnailWidth: Int,
    @Value("\${app.thumbnail.height:512}") private val thumbnailHeight: Int
) : PictureService {

    private val bucket: String get() = s3Properties.bucket
    private val publicPrefix: String get() = s3Properties.resolvedPublicUrlPrefix()

    private val allowedTypes = setOf(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    )
    private val maxFileSize: Long = 16 * 1024 * 1024

    companion object {
        private const val ORIGINAL_PREFIX = "originals"
        private const val THUMBNAIL_PREFIX = "thumbnails"
    }

    override fun findAll(pageable: Pageable): Page<PictureVo> =
        pictureRepository.findAll(pageable).map { it.toVo() }

    override fun findGalleryPictures(pageable: Pageable): Page<PictureVo> =
        pictureRepository.findByShowInGalleryTrue(pageable).map { it.toVo() }

    override fun findById(id: Long): PictureVo =
        pictureRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Picture not found with id $id") }
            .toVo()

    @Transactional
    override fun upload(
        userPrincipal: UserPrincipal,
        file: MultipartFile,
        alt: String?,
        tagIds: Set<Long>?,
        showInGallery: Boolean
    ): PictureVo {
        require(!file.isEmpty) { "File is empty" }
        require(file.size <= maxFileSize) { "File size exceeds limit of 16MB" }
        require(allowedTypes.contains(file.contentType)) {
            "File type '${file.contentType}' is not allowed"
        }

        val originalFilename = file.originalFilename ?: "unknown"
        val extension = contentTypeToExtension(file.contentType ?: "image/jpeg")
        val filename = "${UUID.randomUUID()}.$extension"
        val originalKey = "$ORIGINAL_PREFIX/$filename"

        uploadToS3(
            originalKey,
            file.inputStream,
            file.size,
            file.contentType ?: "image/jpeg"
        )

        val thumbnailUrl = if (file.contentType != "image/svg+xml") {
            val thumbnailKey = "$THUMBNAIL_PREFIX/$filename"
            val thumbBytes = generateThumbnail(file, extension)
            uploadToS3(
                thumbnailKey,
                ByteArrayInputStream(thumbBytes),
                thumbBytes.size.toLong(),
                extensionToContentType(extension)
            )
            "$publicPrefix/$thumbnailKey"
        } else null

        val tags = tagIds?.let { tagRepository.findAllById(it).toMutableSet() } ?: mutableSetOf<Tag>()

        val picture = Picture(
            filename = filename,
            originalFilename = originalFilename,
            mimeType = file.contentType ?: "image/jpeg",
            size = file.size,
            url = "$publicPrefix/$originalKey",
            thumbnailUrl = thumbnailUrl,
            alt = alt,
            showInGallery = showInGallery,
            tags = tags,
            uploaderId = userPrincipal.id
        )

        return pictureRepository.save(picture).toVo()
    }

    @Transactional
    override fun updateMetadata(
        id: Long,
        alt: String?,
        tagIds: Set<Long>?,
        showInGallery: Boolean?
    ): PictureVo {
        val picture = pictureRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Picture not found with id $id") }

        alt?.let { picture.alt = it }
        showInGallery?.let { picture.showInGallery = it }
        tagIds?.let { picture.tags = tagRepository.findAllById(it).toMutableSet() }

        return pictureRepository.save(picture).toVo()
    }

    @Transactional
    override fun delete(id: Long) {
        val picture = pictureRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Picture not found with id $id") }

        deleteFromS3("$ORIGINAL_PREFIX/${picture.filename}")
        picture.thumbnailUrl?.let {
            deleteFromS3("$THUMBNAIL_PREFIX/${picture.filename}")
        }

        pictureRepository.delete(picture)
    }

    private fun uploadToS3(key: String, input: java.io.InputStream, size: Long, contentType: String) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build(),
            RequestBody.fromInputStream(input, size)
        )
    }

    private fun deleteFromS3(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        )
    }

    private fun generateThumbnail(file: MultipartFile, outputFormat: String): ByteArray {
        val output = ByteArrayOutputStream()
        Thumbnails.of(file.inputStream)
            .size(thumbnailWidth, thumbnailHeight)
            .keepAspectRatio(true)
            .outputFormat(outputFormat)
            .toOutputStream(output)
        return output.toByteArray()
    }

    private fun contentTypeToExtension(contentType: String): String = when (contentType) {
        "image/png" -> "png"
        "image/gif" -> "gif"
        "image/webp" -> "webp"
        "image/svg+xml" -> "svg"
        else -> "jpg"
    }

    private fun extensionToContentType(extension: String): String = when (extension) {
        "jpg" -> "image/jpeg"
        else -> "image/$extension"
    }
}