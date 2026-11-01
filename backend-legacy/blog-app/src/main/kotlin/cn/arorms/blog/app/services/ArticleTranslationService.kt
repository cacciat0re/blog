package cn.arorms.blog.app.services

import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleTranslationUpsertRequest
import cn.arorms.blog.common.responses.ArticleTranslationAdminVo
import reactor.core.publisher.Flux

/**
 * @author Sheng
 * @version 1.2.0 2026-09-22
 * @since 2026-09-18
 */
interface ArticleTranslationService {
    /**
     * Save article content(translation)
     */
    fun upsertTranslation(articleId: Long, request: ArticleTranslationUpsertRequest)

    /**
     * Get all translations of an article (admin fine-grained management)
     */
    fun getTranslations(articleId: Long): List<ArticleTranslationAdminVo>

    /**
     * Get a single translation of an article by language
     */
    fun getTranslation(articleId: Long, language: Language): ArticleTranslationAdminVo

    /**
     * Delete a single translation of an article by language
     */
    fun deleteTranslation(articleId: Long, language: Language)

    /**
     * Translate the saved original article title into the target language (non-streaming)
     */
    fun translateTitle(articleId: Long, targetLanguage: Language): String

    /**
     * Translate the saved original article summary into the target language
     * (non-streaming); empty string if the original has no summary
     */
    fun translateSummary(articleId: Long, targetLanguage: Language): String

    /**
     * Translate the saved original article content into the target language,
     * streaming translated markdown chunks
     */
    fun translateContent(articleId: Long, targetLanguage: Language): Flux<String>
}