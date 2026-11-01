package cn.arorms.blog.app.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @version 1.0.0 2026-08-24
 */
@ConfigurationProperties("storage.s3")
data class S3Properties(
    val endpoint: String,
    val bucket: String,
    val accessKey: String,
    val secretKey: String,
    val region: String = "us-east-1",
    val pathStyle: Boolean = true,
    val publicUrlPrefix: String? = null
) {
    fun resolvedPublicUrlPrefix(): String = publicUrlPrefix?.trim()?.takeIf { it.isNotBlank() }
        ?: "$endpoint/$bucket"
}