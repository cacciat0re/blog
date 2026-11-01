package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.entities.Tag
import cn.arorms.blog.app.services.TagService
import cn.arorms.blog.common.requests.TagUpsertRequest
import cn.arorms.blog.common.responses.TagVo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @version 0.9.1 2026-08-03
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/api/admin/tags")
class TagAdminController (private val tagService: TagService) {
    /**
     * @return List<Tag>, raw entity data for admin
     */
    @GetMapping
    fun getAllTag(): ResponseEntity<List<Tag>> {
        val tags = tagService.findAllEntities()
        return ResponseEntity.ok(tags)
    }

    /**
     * @param request
     * @return TagVo
     */
    @PostMapping
    fun createTag(@RequestBody request: TagUpsertRequest): ResponseEntity<TagVo> {
        val savedTag = tagService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTag)
    }

    /**
     * @param request
     * @return TagVo
     */
    @PutMapping("/{id}")
    fun updateTag(@PathVariable id: Long, @RequestBody request: TagUpsertRequest): ResponseEntity<TagVo> {
        val updatedTag = tagService.update(id, request)
        return ResponseEntity.ok(updatedTag)
    }

    @DeleteMapping("/{id}")
    fun deleteTag(@PathVariable id: Long): ResponseEntity<Void> {
        tagService.delete(id)
        return ResponseEntity.noContent().build()
    }
}