package cn.arorms.blog.app.controllers.admin

import cn.arorms.blog.app.services.ArticleTranslationService
import cn.arorms.blog.common.enums.Language
import cn.arorms.blog.common.requests.ArticleTranslationUpsertRequest
import cn.arorms.blog.common.responses.ArticleTranslationAdminVo
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

/**
 * Admin ArticleTranslation Controller
 * Fine-grained management of a single article translation,
 * decoupled from the article metadata endpoints
 * @author cacc
 * @version 1.2.0 2026-09-21
 * @since 2026-09-20
 */
@RestController
@RequestMapping("/api/admin/articles/{articleId}/translations")
class ArticleTranslationAdminController(
    private val articleTranslationService: ArticleTranslationService,
) {
    /**
     * List all translations of an article
     */
    @GetMapping
    fun getTranslations(@PathVariable articleId: Long): ResponseEntity<List<ArticleTranslationAdminVo>> =
        ResponseEntity.ok(articleTranslationService.getTranslations(articleId))

    /**
     * Get a single translation by language
     */
    @GetMapping("/{language}")
    fun getTranslation(
        @PathVariable articleId: Long,
        @PathVariable language: Language
    ): ResponseEntity<ArticleTranslationAdminVo> =
        ResponseEntity.ok(articleTranslationService.getTranslation(articleId, language))

    /**
     * Create or update a single translation (language is carried in the body).
     * The admin frontend renders originalContent (markdown) to HTML on save
     * and passes both originalContent and content.
     */
    @PutMapping
    fun upsertTranslation(
        @PathVariable articleId: Long,
        @RequestBody request: ArticleTranslationUpsertRequest
    ): ResponseEntity<Void> {
        articleTranslationService.upsertTranslation(articleId, request)
        return ResponseEntity.noContent().build()
    }

    /**
     * Delete a single translation by language
     */
    @DeleteMapping("/{language}")
    fun deleteTranslation(
        @PathVariable articleId: Long,
        @PathVariable language: Language
    ): ResponseEntity<Void> {
        articleTranslationService.deleteTranslation(articleId, language)
        return ResponseEntity.noContent().build()
    }

    /**
     * Translate the saved original article title into the target language
     * (non-streaming). Triggered together with summary/content by AI translate.
     */
    @PutMapping("/translate/title", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun translateTitle(
        @PathVariable articleId: Long,
        @RequestBody targetLanguage: Language
    ): ResponseEntity<String> =
        ResponseEntity.ok(articleTranslationService.translateTitle(articleId, targetLanguage))

    /**
     * Translate the saved original article summary into the target language
     * (non-streaming); empty body if the original has no summary
     */
    @PutMapping("/translate/summary", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun translateSummary(
        @PathVariable articleId: Long,
        @RequestBody targetLanguage: Language
    ): ResponseEntity<String> =
        ResponseEntity.ok(articleTranslationService.translateSummary(articleId, targetLanguage))

    /**
     * Translate the saved original article content into the target language,
     * streaming translated markdown chunks as SSE for the editor
     */
    @PutMapping("/translate/content", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun translateContent(
        @PathVariable articleId: Long,
        @RequestBody targetLanguage: Language
    ): Flux<String> =
        articleTranslationService.translateContent(articleId, targetLanguage)
}