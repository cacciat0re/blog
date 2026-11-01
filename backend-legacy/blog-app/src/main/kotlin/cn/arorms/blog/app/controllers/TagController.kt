package cn.arorms.blog.app.controllers

import cn.arorms.blog.common.responses.TagVo
import cn.arorms.blog.app.services.TagService
import cn.arorms.blog.common.enums.Language
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for Tag operations
 * @version 0.9.1 2026-08-03
 * @author cacc
 */
@RestController
@RequestMapping("/api/tags")
class TagController(private val tagService: TagService) {

    /**
     * @return List<TagVo>
     */
    @GetMapping
    fun getAllTags(
        @RequestParam(defaultValue = "EN") language: Language
    ): ResponseEntity<List<TagVo>> {
        val tags = tagService.findAll(language)
        return ResponseEntity.ok(tags)
    }
}