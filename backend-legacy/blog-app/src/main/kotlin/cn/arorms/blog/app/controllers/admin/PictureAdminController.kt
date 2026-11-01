package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.services.PictureService
import cn.arorms.blog.common.requests.PictureUpdateRequest
import cn.arorms.blog.common.responses.PictureVo
import cn.arorms.framework.common.domain.PageResponse
import cn.arorms.framework.security.UserPrincipal
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * REST Controller for Picture operations
 * @version 1.1.0 2026-08-30
 * @since 2026-08-24
 */
@RestController
@RequestMapping("/api/admin/pictures")
class PictureAdminController(private val pictureService: PictureService) {

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String
    ): ResponseEntity<PageResponse<PictureVo>> {
        val sort = if (sortDir == "asc") Sort.by(sortBy).ascending() else Sort.by(sortBy).descending()
        val pageable = PageRequest.of(page, size, sort)
        val result = pictureService.findAll(pageable)
        return ResponseEntity.ok(PageResponse.fromPage(result))
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<PictureVo> =
        ResponseEntity.ok(pictureService.findById(id))

    @PostMapping("/upload")
    fun upload(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) alt: String?,
        @RequestParam(required = false) tagIds: Set<Long>?,
        @RequestParam(required = false, defaultValue = "false") showInGallery: Boolean
    ): ResponseEntity<PictureVo> {
        val vo = pictureService.upload(userPrincipal, file, alt, tagIds, showInGallery)
        return ResponseEntity.status(HttpStatus.CREATED).body(vo)
    }

    @PutMapping("/{id}")
    fun updateMetadata(
        @PathVariable id: Long,
        @RequestBody request: PictureUpdateRequest
    ): ResponseEntity<PictureVo> {
        val vo = pictureService.updateMetadata(id, request.alt, request.tagIds, request.showInGallery)
        return ResponseEntity.ok(vo)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        pictureService.delete(id)
        return ResponseEntity.noContent().build()
    }
}