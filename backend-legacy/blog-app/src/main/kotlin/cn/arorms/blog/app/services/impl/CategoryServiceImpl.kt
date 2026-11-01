package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.Category
import cn.arorms.blog.app.repositories.ArticleRepository
import cn.arorms.blog.app.repositories.CategoryRepository
import cn.arorms.blog.app.services.CategoryService
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.CategoryUpsertRequest
import cn.arorms.blog.common.responses.CategoryTreeNode
import cn.arorms.blog.common.responses.CategoryVo
import cn.arorms.framework.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Category service implementation
 * @version 1.2.0 2026-09-08
 * @since 2026-02-05
 * @author cacc
 */
@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val articleRepository: ArticleRepository
) : CategoryService {

    override fun findAll(language: Language): List<CategoryVo> {
        val rows = categoryRepository.findAllLocalized(language.name)
        return rows.map { row ->
            CategoryVo(
                id = (row[0] as Number).toLong(),
                name = row[1] as? String ?: "",
                slug = row[2] as String,
                parentId = (row[3] as? Number)?.toLong()
            )
        }
    }

    override fun findById(id: Long, language: Language): CategoryVo {
        val category = categoryRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Category not found with id: $id") }
        return category.toDto(language)
    }

    override fun findBySlug(slug: String, language: Language): CategoryVo {
        val category = categoryRepository.findBySlug(slug)
            ?: throw ResourceNotFoundException("Category not found with slug: $slug")
        return category.toDto(language)
    }

    override fun findAllEntities(): List<Category> = categoryRepository.findAll()

    override fun findRootCategories(): List<Category> = categoryRepository.findByParentId(null)

    override fun findChildren(parentId: Long): List<Category> = categoryRepository.findByParentId(parentId)

    /**
     * Build full category tree with localized names
     */
    override fun buildCategoryTree(language: Language): CategoryTreeNode {
        val rootCategories = findRootCategories()
        val children = rootCategories.map { buildTree(it, language) }

        return CategoryTreeNode(
            id = -1,
            name = "ARORMS.BLOG",
            slug = "root",
            children = children
        )
    }

    private fun buildTree(category: Category, language: Language): CategoryTreeNode {
        val children = findChildren(category.id!!)
        val childNodes = children.map { buildTree(it, language) }

        val name = category.names[language] ?: category.names[Language.EN] ?: category.names[Language.ZH] ?: ""

        return CategoryTreeNode(
            id = category.id!!,
            name = name,
            slug = category.slug,
            children = childNodes
        )
    }

    @Transactional
    override fun create(request: CategoryUpsertRequest): CategoryVo {
        if (categoryRepository.existsBySlug(request.slug)) {
            throw IllegalArgumentException("Category with slug '${request.slug}' already exists")
        }
        val parent = request.parentId?.let { id ->
            categoryRepository.findById(id).orElseThrow {
                ResourceNotFoundException("Parent category with id $id not found")
            }
        }
        val category = Category(
            names = request.names.toMutableMap(),
            slug = request.slug,
            parent = parent
        )
        val saved = categoryRepository.save(category)
        return saved.toDto(Language.EN)
    }

    @Transactional
    override fun update(id: Long, request: CategoryUpsertRequest): CategoryVo {
        val existingCategory = categoryRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Category not found with id: $id") }

        if (request.slug != existingCategory.slug && categoryRepository.existsBySlug(request.slug)) {
            throw IllegalArgumentException("Category with slug '${request.slug}' already exists")
        }

        existingCategory.names = request.names.toMutableMap()
        existingCategory.slug = request.slug
        existingCategory.parent = request.parentId?.let { categoryRepository.getReferenceById(it) }

        val savedCategory = categoryRepository.save(existingCategory)
        return savedCategory.toDto(Language.EN)
    }

    @Transactional
    override fun delete(id: Long) {
        if (!categoryRepository.existsById(id)) {
            throw IllegalArgumentException("Category not found with id: $id")
        }

        // Check if category has children
        val children = categoryRepository.findByParentId(id)
        if (children.isNotEmpty()) {
            throw IllegalStateException("Cannot delete category with children. Remove or reassign children first.")
        }

        // Remove category from articles (set to null)
        val articles = articleRepository.findAll().filter { it.category?.id == id }
        articles.forEach { it.category = null }

        categoryRepository.deleteById(id)
    }

    override fun getArticleCount(categoryId: Long): Long {
        return articleRepository.countByCategoryId(categoryId)
    }

    private fun Category.toDto(language: Language): CategoryVo {
        val name = names[language] ?: names[Language.EN] ?: names[Language.ZH] ?: ""
        return CategoryVo(
            id = id,
            name = name,
            slug = slug,
            parentId = parent?.id,
        )
    }
}
