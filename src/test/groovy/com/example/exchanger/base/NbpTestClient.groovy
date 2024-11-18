package com.example.exchanger.base

import com.example.exchanger.exchangerate.infrastructure.nbp.ExchangeRateDto
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class NbpTestClient {

    private final static String PATH = "/api/exchangerates/rates/a/usd"
    private final static String JSON_FORMAT = "json"
    private final int port
    private final WebTestClient webTestClient

    NbpTestClient(Integer port, WebTestClient webTestClient) {
        this.port = port
        this.webTestClient = webTestClient
    }

    ExchangeRateDto getExchangeRateExpectingSuccess() {
        return get()
            .expectStatus().isOk()
            .expectBody(ExchangeRateDto.class)
            .returnResult()
            .responseBody
    }

    ErrorResponse getExchangeRateExpectingError(
        HttpStatus expectedErrorStatus
    ) {
        return get()
            .expectStatus()
            .isEqualTo(expectedErrorStatus)
            .expectBody(ErrorResponse.class)
            .returnResult()
            .responseBody
    }

    private WebTestClient.ResponseSpec get() {
        webTestClient.get()
            .uri(uri -> uri
                .port(3333)
                .path(PATH)
                .queryParam("format", JSON_FORMAT)
                .build())
            .header(ACCEPT, APPLICATION_JSON_VALUE)
            .exchange()
    }

}
