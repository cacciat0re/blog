package cn.arorms.blog.app.entities

import com.fasterxml.jackson.annotation.JsonIncludeProperties
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Image entity for resource management
 * @version 1.0.0 2026-08-27
 * @since 2026-03-09
 */
@Entity
@Table(name = "pictures")
class Picture(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 255)
    var filename: String,

    @Column(nullable = false, length = 255)
    var originalFilename: String,

    @Column(nullable = false, length = 100)
    var mimeType: String,

    @Column(nullable = false)
    var size: Long, // File size in bytes

    @Column(nullable = false, length = 500)
    var url: String, // Access URL

    @Column(length = 500)
    var thumbnailUrl: String? = null, // Thumbnail URL

    @Column(length = 500)
    var alt: String? = null, // Alt text for accessibility

    @Column(name = "uploader_id")
    var uploaderId: String,

    @ManyToMany
    @JoinTable(
        name = "picture_tags_map",
        joinColumns = [JoinColumn(name = "picture_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @Column(name = "show_in_gallery")
    var showInGallery: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)