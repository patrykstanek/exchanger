package com.example.exchanger.base

import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient

abstract class MvcIntegrationSpec extends IntegrationSpec {

    @Autowired
    protected WebTestClient webTestClient

    protected NbpTestClient nbpTestClient

    @Before
    void setupHttpClient() {
        nbpTestClient = new NbpTestClient(wireMockPort, webTestClient)
    }
}
