package cn.arorms.blog.app.config

import cn.arorms.framework.security.KeycloakAuthenticationConverter
import cn.arorms.framework.security.SecurityAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security configuration for JWT authentication
 * @author Sheng
 * @version 1.2.1 2026-09-22
 * @since 2026-07-22
 */
@Configuration
@EnableWebSecurity
@Import(SecurityAutoConfiguration::class)
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, converter: KeycloakAuthenticationConverter<*>): SecurityFilterChain {
        return http.csrf { it.disable() }.authorizeHttpRequests { auth ->
            auth.requestMatchers("/api/admin/auth/me").authenticated()
                .requestMatchers("/api/admin/**").hasRole("admin")
                .anyRequest().permitAll()
        }.oauth2ResourceServer { oauth2 ->
            oauth2.jwt { jwt ->
                jwt.jwtAuthenticationConverter(converter)
            }
        }.build()
    }
}
