package com.example.exchanger.exchangerate.infrastructure.nbp

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.ServeEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

import java.time.Duration

import static com.example.exchanger.exchangerate.infrastructure.nbp.NbpResponse.exchangeRateResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Component
class NbpStubbingTestService {

    private final static String PATH = "/api/exchangerates/rates/a/usd"
    private final static String JSON_FORMAT = "json"
    private final WireMockServer wireMock
    private final Duration nbpClientTimeout

    NbpStubbingTestService(WireMockServer wireMock, @Value("\${dependencies.nbp.timeout}") Duration nbpClientTimeout) {
        this.wireMock = wireMock
        this.nbpClientTimeout = nbpClientTimeout
    }

    void stubExchangeRateSucceededResponse(String content = exchangeRateResponse()) {
        stubSucceededResponse(createUri(), content)
    }

    void stubExchangeRateEmptyResponse() {
        stubSucceededResponse(createUri(), "")
    }

    void stubExchangeRateErrorResponse(HttpStatus httpStatus) {
        stubErrorResponse(createUri(), httpStatus)
    }

    List<ServeEvent> getAllInteractions() {
        return wireMock.getAllServeEvents()
    }

    private void stubSucceededResponse(String uri, String content) {
        wireMock.stubFor(
            WireMock.get(uri)
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(WireMock.okJson(content))
        )
    }

    private void stubErrorResponse(String uri, HttpStatus httpStatus) {
        wireMock.stubFor(
            WireMock.get(uri)
                .withHeader(ACCEPT, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(httpStatus.value())
                )
        )
    }

    private static String createUri() {
        return PATH.concat("?format=%s").formatted(JSON_FORMAT)
    }
}
