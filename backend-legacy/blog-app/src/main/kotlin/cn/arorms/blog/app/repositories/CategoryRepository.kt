package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for Category entity
 */
@Repository
interface CategoryRepository : JpaRepository<Category, Long> {

    @Query(value = "SELECT c.id, c.names ->> cast(:lang as text), c.slug, c.parent_id FROM categories c WHERE c.names ->> cast(:lang as text) IS NOT NULL", nativeQuery = true)
    fun findAllLocalized(@Param("lang") lang: String): List<Array<Any>>

    fun findBySlug(slug: String): Category?

    fun findByParentId(parentId: Long?): List<Category>

    fun existsBySlug(slug: String): Boolean
}
