package cn.arorms.blog.app.repositories

import cn.arorms.blog.app.entities.ArticleTranslation
import cn.arorms.blog.common.enums.Language
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @version 1.2.0 2026-09-18
 * @since 2026-09-18
 */
interface ArticleTranslationRepository :
    JpaRepository<ArticleTranslation, Long>,
    ArticleTranslationRepositoryCustom {
    fun findByArticle_Id(articleId: Long): List<ArticleTranslation>

    fun findByArticle_IdAndLanguage(articleId: Long, language: Language): ArticleTranslation?

    fun deleteByArticle_IdAndLanguage(articleId: Long, language: Language): Long
}