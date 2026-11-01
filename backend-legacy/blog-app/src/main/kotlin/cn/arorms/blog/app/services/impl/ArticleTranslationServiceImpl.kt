package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.entities.ArticleTranslation
import cn.arorms.blog.app.repositories.ArticleRepository
import cn.arorms.blog.app.repositories.ArticleTranslationRepository
import cn.arorms.blog.app.services.ArticleTranslationService
import cn.arorms.blog.app.services.LlmService
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleTranslationUpsertRequest
import cn.arorms.blog.common.responses.ArticleTranslationAdminVo
import cn.arorms.framework.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux

/**
 * @author Sheng
 * @version 1.2.0 2026-09-22
 * @since 2026-09-18
 */
@Service
class ArticleTranslationServiceImpl (
    private val articleTranslationRepository: ArticleTranslationRepository,
    private val articleRepository: ArticleRepository,
    private val llmService: LlmService
) : ArticleTranslationService {

    @Transactional
    override fun upsertTranslation(articleId: Long, request: ArticleTranslationUpsertRequest) {
        // Lightweight save (e.g. autosave on language switch) leaves content null:
        // keep the previously rendered HTML instead of wiping it
        val translationId = request.id
        val existingContent = if (request.content == null && translationId != null) {
            articleTranslationRepository.findById(translationId)
                .map { it.content }
                .orElse("")
        } else {
            null
        }
        val articleTranslation = ArticleTranslation (
            id = request.id,
            article = articleRepository.getReferenceById(articleId),
            language = request.language,
            title = request.title,
            summary = request.summary,
            originalContent = request.originalContent,
            content = request.content ?: existingContent ?: "",
            isAiTranslated = request.isAiTranslated ?: false
        )
        articleTranslationRepository.save(articleTranslation)
    }

    @Transactional(readOnly = true)
    override fun getTranslations(articleId: Long): List<ArticleTranslationAdminVo> =
        articleTranslationRepository.findByArticle_Id(articleId).map { it.toAdminVo() }

    @Transactional(readOnly = true)
    override fun getTranslation(articleId: Long, language: Language): ArticleTranslationAdminVo =
        (articleTranslationRepository.findByArticle_IdAndLanguage(articleId, language)
            ?: throw ResourceNotFoundException("Translation not found for article $articleId in language $language"))
            .toAdminVo()

    @Transactional
    override fun deleteTranslation(articleId: Long, language: Language) {
        val deleted = articleTranslationRepository.deleteByArticle_IdAndLanguage(articleId, language)
        if (deleted == 0L) {
            throw ResourceNotFoundException("Translation not found for article $articleId in language $language")
        }
    }

    /**
     * Translate article title by LLM (non-streaming)
     */
    override fun translateTitle(articleId: Long, targetLanguage: Language): String =
        llmService.translate(getOriginalTranslation(articleId).title, targetLanguage)

    /**
     * Translate article summary by LLM (non-streaming)
     */
    override fun translateSummary(articleId: Long, targetLanguage: Language): String {
        val summary = getOriginalTranslation(articleId).summary
        return if (summary.isNullOrBlank()) "" else llmService.translate(summary, targetLanguage)
    }

    /**
     * Translate article content by LLM, streaming translated chunks
     */
    override fun translateContent(articleId: Long, targetLanguage: Language): Flux<String> =
        llmService.translateStream(getOriginalTranslation(articleId).originalContent, targetLanguage)

    private fun getOriginalTranslation(articleId: Long): ArticleTranslation =
        articleTranslationRepository.getOriginalTranslation(articleId)
            ?: throw ResourceNotFoundException("No original translation found for article $articleId")

    private fun ArticleTranslation.toAdminVo() = ArticleTranslationAdminVo(
        id = id,
        language = language,
        title = title,
        summary = summary,
        originalContent = originalContent,
        isAiTranslated = isAiTranslated
    )
}