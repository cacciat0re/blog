package cn.arorms.blog.app.controllers

import cn.arorms.blog.app.services.PictureService
import cn.arorms.blog.common.responses.PictureVo
import cn.arorms.framework.common.domain.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Gallery Controller
 * @version 1.1.0 2026-08-30
 * @since 2026-04-23
 */
@RestController
@RequestMapping("/api/pictures")
class PictureController(private val pictureService: PictureService) {

    // Gallery API
    @GetMapping
    fun getGalleryPictures(
        pageable: Pageable,
    ): ResponseEntity<PageResponse<PictureVo>> {
        val page = pictureService.findGalleryPictures(pageable)
        val pageResponse = PageResponse.fromPage(page)
        return ResponseEntity.ok(pageResponse)
    }
}