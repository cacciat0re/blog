package cn.arorms.blog.app.config

import io.netty.channel.ChannelOption
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.client.HttpGraphQlClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient
import java.time.Duration

/**
 * @version 1.1.2 2026-09-01
 * @since 2026-05-10
 */
@Configuration
class GraphQlConfig (
    @Value($$"${application.cloudflare.token}")
    private var cloudflareToken: String
) {
    @Bean
    fun cloudflareClient(): HttpGraphQlClient {
        val httpClient = HttpClient.create()
            .resolver(DefaultAddressResolverGroup.INSTANCE)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15_000)
            .responseTimeout(Duration.ofSeconds(30))

        val webClient = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl("https://api.cloudflare.com/client/v4/graphql")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $cloudflareToken")
            .build()
        return HttpGraphQlClient.builder(webClient).build()
    }
}