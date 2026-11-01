package cn.arorms.blog.app.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * CorsConfig.kt
 * @author cacc
 * @version 1.2.7 2026-10.13
 * @since 2026-03-12
 */

@Configuration
class CorsConfig(
    @Value("\${application.cors.allowed-origins}")
    private val allowedOrigins: Array<String>,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        val origins = allowedOrigins
        registry.addMapping("/**")
            .allowedOrigins(*origins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}