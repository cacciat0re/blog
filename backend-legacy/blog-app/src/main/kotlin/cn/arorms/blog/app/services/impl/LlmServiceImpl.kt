package cn.arorms.blog.app.services.impl

import cn.arorms.blog.app.services.LlmService
import cn.arorms.blog.common.enums.Language
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

/**
 * LLM service implementation
 * @version 1.2.0 2026-09-22
 * @since 2026-05-10
 * @author Sheng
 */
@Service
class LlmServiceImpl(chatModel: ChatModel) : LlmService {

    private val chatClient: ChatClient = ChatClient.builder(chatModel).build()

    override fun call(prompt: String): String {
        return chatClient.prompt(prompt).call().content().toString()
    }

    override fun callWithoutThinking(prompt: String) {
        TODO("Not yet implemented")
    }

    override fun stream(prompt: String): Flux<String> {
        TODO("Not yet implemented")
    }

    override fun streamWithoutThinking(prompt: String): Flux<String> {
        return chatClient.prompt(prompt).options(
            OpenAiChatOptions.builder().extraBody(mapOf("thinking" to mapOf("type" to "disabled")))
        ).stream().content()
    }

    override fun translate(text: String, targetLanguage: Language): String =
        chatClient.prompt(shortTextPrompt(text, targetLanguage)).options(noThinkingOptions()).call().content()
            ?: throw IllegalStateException("LLM returned an empty translation result")

    override fun translateStream(text: String, targetLanguage: Language): Flux<String> =
        chatClient.prompt(contentPrompt(text, targetLanguage)).options(noThinkingOptions()).stream().content()

    private fun noThinkingOptions() =
        OpenAiChatOptions.builder().extraBody(mapOf("thinking" to mapOf("type" to "disabled")))

    private fun shortTextPrompt(text: String, targetLanguage: Language) = """
        You are an extremely precise professional translation expert. Translate the text I provide into $targetLanguage.
        **Strict Constraints:**
        1. Forbidden to change words: Strictly forbidden to modify, replace, optimize, or polish any of the original words I provide.
        2. Literal translation priority: Maintain the word order and structure of the original sentences.
        3. No explanation: Directly output the translation result, nothing else. No quotes, no preamble.

        The text is as follows:
        $text
    """.trimIndent()

    private fun contentPrompt(content: String, targetLanguage: Language) = """
        You are an extremely precise professional translation expert. Translate the content I provide into $targetLanguage.
        **Strict Constraints:**
        1. Forbidden to change words: Strictly forbidden to modify, replace, optimize, or polish any of the original words I provide.
        2. Literal translation priority: Maintain the word order and structure of the original sentences.
        3. Format retention: Retain original punctuation, line breaks, and indentations, including markdown symbols and formatting.
        4. Math and code untouched: Keep inline math (${'$'}...${'$'}), display math (${'$'}${'$'}...${'$'}${'$'}), inline code backticks, and code fence blocks exactly as-is.
        5. No explanation: Directly output the translated markdown only. Do NOT wrap the output in code fences, do NOT output any text before or after the translation.

        The content is as follows:
        $content
    """.trimIndent()
}