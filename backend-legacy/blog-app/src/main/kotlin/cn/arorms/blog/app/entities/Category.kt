package cn.arorms.blog.app.entities

import cn.arorms.blog.common.enums.Language
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "names", columnDefinition = "jsonb")
    var names: MutableMap<Language, String> = mutableMapOf(),

    @Column(unique = true, length = 50)
    var slug: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIncludeProperties("id")
    var parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    @JsonIgnore
    var children: MutableSet<Category> = mutableSetOf(),

//    @OneToMany(mappedBy = "category")
//    @JsonIgnore
//    var articles: MutableSet<Article> = mutableSetOf()
)
