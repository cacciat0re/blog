package cn.arorms.blog.app.config

import cn.arorms.blog.app.properties.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

/**
 * @version 1.0.0 2026-08-24
 * @since 2026-08-24
 */
@Configuration
class S3Config (
    private val properties: S3Properties
) {
    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .endpointOverride(URI.create(properties.endpoint))
        .region(Region.of(properties.region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
            )
        )
        .serviceConfiguration { cfg -> cfg.pathStyleAccessEnabled(properties.pathStyle) }
        .build()
}