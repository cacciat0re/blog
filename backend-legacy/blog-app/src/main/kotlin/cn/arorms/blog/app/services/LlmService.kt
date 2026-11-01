package cn.arorms.blog.app.services

import cn.arorms.blog.common.enums.Language
import reactor.core.publisher.Flux

/**
 * LLM service interface
 * @version 1.2.0 2026-09-22
 * @since 2026-05-10
 * @author Sheng
 */
interface LlmService {
    fun call(prompt: String): String
    fun callWithoutThinking(prompt: String)
    fun stream(prompt: String): Flux<String>
    fun streamWithoutThinking(prompt: String): Flux<String>

    /**
     * Non-streaming translation of a short text (article title, summary).
     * Returns the translated text only.
     */
    fun translate(text: String, targetLanguage: Language): String

    /**
     * Streaming translation of long Markdown content; emits translated chunks
     */
    fun translateStream(text: String, targetLanguage: Language): Flux<String>
}