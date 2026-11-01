package cn.arorms.blog.app.services

import cn.arorms.blog.app.entities.Tag
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.TagUpsertRequest
import cn.arorms.blog.common.responses.TagVo

/**
 * Tag service interface
 * @version 1.2.0 2026-09-08
 * @since 2026-03-09
 * @author cacc
 */
interface TagService {

    fun findAll(language: Language): List<TagVo>

    fun findAllEntities(): List<Tag>

    fun findById(id: Long): Tag?

    fun findById(id: Long, language: Language): TagVo

    fun getTagsByIds(idList: List<Long>): List<Tag>

    fun findBySlug(slug: String): Tag?

    fun findBySlug(slug: String, language: Language): TagVo

    fun create(request: TagUpsertRequest): TagVo

    fun update(id: Long, request: TagUpsertRequest): TagVo

    fun delete(id: Long)
}
