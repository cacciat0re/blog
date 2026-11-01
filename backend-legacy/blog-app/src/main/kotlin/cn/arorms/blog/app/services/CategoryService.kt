package cn.arorms.blog.app.services

import cn.arorms.blog.app.entities.Category
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.CategoryUpsertRequest
import cn.arorms.blog.common.responses.CategoryTreeNode
import cn.arorms.blog.common.responses.CategoryVo

/**
 * Category service interface
 * @version 1.2.0 2026-09-08
 * @since 2026-02-05
 * @author cacc
 */
interface CategoryService {

    fun findAll(language: Language): List<CategoryVo>

    fun findById(id: Long, language: Language): CategoryVo

    fun findBySlug(slug: String, language: Language): CategoryVo

    fun findAllEntities(): List<Category>

    fun findRootCategories(): List<Category>

    fun findChildren(parentId: Long): List<Category>

    fun buildCategoryTree(language: Language): CategoryTreeNode

    fun create(request: CategoryUpsertRequest): CategoryVo

    fun update(id: Long, request: CategoryUpsertRequest): CategoryVo

    fun delete(id: Long)

    fun getArticleCount(categoryId: Long): Long
}
