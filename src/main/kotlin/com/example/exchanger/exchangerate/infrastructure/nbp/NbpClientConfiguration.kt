package com.example.exchanger.exchangerate.infrastructure.nbp;

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
@EnableConfigurationProperties(NbpClientProperties::class)
internal class NbpClientConfiguration {

    @Bean
    fun nbpWebClient(properties: NbpClientProperties): WebClient =
        createWebClient(properties)

    private fun createWebClient(properties: NbpClientProperties): WebClient {
        return WebClient
            .builder()
            .clientConnector(webClientConfig(properties))
            .baseUrl(properties.url)
            .build()
    }

    private fun webClientConfig(properties: NbpClientProperties): ReactorClientHttpConnector {
        val provider = ConnectionProvider
            .builder(properties.name)
            .maxConnections(properties.maxConnections)
            .maxLifeTime(properties.timeout)
            .build()
        return ReactorClientHttpConnector(HttpClient.create(provider))
    }
}

@ConfigurationProperties(prefix = "dependencies.nbp")
internal data class NbpClientProperties(
    val name: String,
    val url: String,
    val timeout: Duration,
    val maxConnections: Int
)
