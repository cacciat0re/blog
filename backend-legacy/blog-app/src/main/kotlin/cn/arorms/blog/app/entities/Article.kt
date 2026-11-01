package cn.arorms.blog.app.entities

import cn.arorms.blog.common.enums.ArticleStatus
import cn.arorms.blog.common.enums.Language
import cn.arorms.framework.common.domain.BaseEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import jakarta.persistence.*

/**
 * Article entity
 * @author cacc
 * @version 1.1.2 2026-09-01
 */
@Entity
@Table(name = "articles")
class Article(

    @Column(name = "slug", unique = true, length = 255, nullable = false)
    var slug: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    var status: ArticleStatus? = ArticleStatus.DRAFT,

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIncludeProperties("id", "name", "slug")
    @JoinColumn(name = "category_id")
    var category: Category? = null,

    @Column(name = "author_id", nullable = false, comment = "User UUID of the author")
    var authorId: String,

    // exposed in the admin API as the natural aggregate wrapper; the rendered
    // HTML (content) stays @JsonIgnore'd on ArticleTranslation itself
    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @MapKeyEnumerated(EnumType.STRING)
    @MapKey(name = "language")
    val translations: MutableMap<Language, ArticleTranslation> = mutableMapOf(),

    @ManyToMany
    @JoinTable(
        name = "article_tags_map",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
) : BaseEntity() {
}
