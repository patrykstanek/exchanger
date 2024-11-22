package com.example.exchanger.infrastructure.mapper

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(objectMapperBuilder: Jackson2ObjectMapperBuilder): ObjectMapper {
        return objectMapperBuilder
            .modulesToInstall(KotlinModule.Builder().build())
            .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .createXmlMapper(false)
            .build()
    }
}
