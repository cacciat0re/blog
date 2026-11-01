package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.Tag
import cn.arorms.blog.app.repositories.TagRepository
import cn.arorms.blog.app.services.TagService
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.TagUpsertRequest
import cn.arorms.blog.common.responses.TagVo
import cn.arorms.framework.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Tag service implementation
 * @version 1.2.0 2026-09-08
 * @since 2026-03-09
 * @author cacc
 */
@Service
class TagServiceImpl(
    private val tagRepository: TagRepository
) : TagService {

    override fun findAll(language: Language): List<TagVo> {
        return tagRepository.findAll().map { it.toVo() }
    }

    override fun findAllEntities(): List<Tag> {
        return tagRepository.findAll()
    }

    override fun findById(id: Long): Tag? {
        return tagRepository.findById(id).orElse(null)
    }

    override fun findById(id: Long, language: Language): TagVo {
        val tag = tagRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Tag not found with id: $id")
        }
        return tag.toVo()
    }

    override fun getTagsByIds(idList: List<Long>): List<Tag> {
        return tagRepository.findAllById(idList)
    }

    override fun findBySlug(slug: String): Tag? {
        return tagRepository.findBySlug(slug)
    }

    override fun findBySlug(slug: String, language: Language): TagVo {
        val tag = tagRepository.findBySlug(slug) ?: throw ResourceNotFoundException("Tag not found with slug: $slug")
        return tag.toVo()
    }

    @Transactional
    override fun create(request: TagUpsertRequest): TagVo {
        if (tagRepository.existsBySlug(request.slug)) {
            throw IllegalArgumentException("Tag with slug '${request.slug}' already exists")
        }
        val tag = Tag(
            name = request.name,
            slug = request.slug
        )
        val saved = tagRepository.save(tag)
        return saved.toVo()
    }

    @Transactional
    override fun update(id: Long, request: TagUpsertRequest): TagVo {
        val existingTag = tagRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Tag not found with id: $id") }

        if (request.slug != existingTag.slug && tagRepository.existsBySlug(request.slug)) {
            throw IllegalArgumentException("Tag with slug '${request.slug}' already exists")
        }

        existingTag.name = request.name
        existingTag.slug = request.slug

        val saved = tagRepository.save(existingTag)
        return saved.toVo()
    }

    @Transactional
    override fun delete(id: Long) {
        if (!tagRepository.existsById(id)) {
            throw ResourceNotFoundException("Tag not found with id: $id")
        }
        tagRepository.deleteById(id)
    }

    private fun Tag.toVo(): TagVo {
        return TagVo(
            id = id,
            name = name,
            slug = slug
        )
    }
}
