package cn.arorms.blog.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 100)
    var name: String,

    @Column(unique = true, length = 50)
    var slug: String,

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    var articles: MutableSet<Article> = mutableSetOf()
)
