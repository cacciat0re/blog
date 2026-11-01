package cn.arorms.blog.common.responses

/**
 * Article detail response Vo
 * @author cacc
 * @version 1.1.2 2026-09-01
 */
import cn.arorms.blog.common.enums.Language
import java.time.Instant

data class ArticleVo(
    val id: Long,
    val slug: String,
    val createdAt: Instant,
    val updatedAt: Instant,
//    val author: UserVo,
    val isAiTranslated: Boolean,
    val title: String,
    val summary: String,
    val content: String,
    val language: Language,
    val category: CategoryVo,
    val tags: List<TagVo>,
)
