package com.example.exchanger.base

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.slf4j.Logger
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

import static org.slf4j.LoggerFactory.getLogger

class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = getLogger(WireMockInitializer.class)
    private static final DEFAULT_PORT = 3333

    @Override
    void initialize(ConfigurableApplicationContext applicationContext) {
        Integer port = applicationContext.environment.getProperty("wiremock.port", Integer.class, DEFAULT_PORT)
        WireMockConfiguration config = WireMockConfiguration
            .wireMockConfig()
            .port(port)
        WireMockServer wireMockServer = new WireMockServer(config)
        wireMockServer.start()
        applicationContext.beanFactory.registerSingleton("wireMock", wireMockServer)
        logger.info("WireMock started at: {}", wireMockServer.baseUrl())
    }
}
