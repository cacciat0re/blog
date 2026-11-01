package cn.arorms.blog.app.entities

import cn.arorms.blog.common.enums.Language
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

/**
 * @version 1.2.0 2026-09-18
 * @since 2026-05-02
 */
@Entity
@Table(
    name = "article_translations",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["article_id", "language"])
    ]
)
class ArticleTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 2, nullable = false)
    val language: Language,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(length = 500)
    var summary: String? = null,

    /**
     * Original markdown content
     */
    @Column(name = "original_content", columnDefinition = "TEXT")
    var originalContent: String,

    /**
     * HTML content; write-only — never serialized back to the admin client,
     * which re-renders markdown to HTML on save
     */
    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    var content: String,

    var isAiTranslated: Boolean? = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    var article: Article
)