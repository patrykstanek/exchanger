package com.example.exchanger.base

import com.example.exchanger.account.infrastructure.http.AccountBalancesDto
import com.example.exchanger.account.infrastructure.http.AccountDetailsDto
import com.example.exchanger.shared.AccountId
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class ExchangerTestClient {

    private final static String CREATE_ACCOUNT_PATH = "/v1/accounts"
    private final static String EXCHANGE_PATH = "/v1/accounts/{accountId}/exchange"
    private final static String ACCOUNT_INFO_PATH = "/v1/accounts/{accountId}"

    private final int port
    private final WebTestClient webTestClient

    ExchangerTestClient(Integer port, WebTestClient webTestClient) {
        this.port = port
        this.webTestClient = webTestClient
    }

    AccountId createAccountExpectingSuccess(String accountRequest) {
        return post(accountRequest)
            .expectStatus().isOk()
            .expectBody(AccountId.class)
            .returnResult()
            .responseBody
    }

    ErrorResponse createAccountExpectingError(
        String accountRequest,
        HttpStatus expectedErrorStatus
    ) {
        return post(accountRequest)
            .expectStatus().isEqualTo(expectedErrorStatus)
            .expectBody(ErrorResponse.class)
            .returnResult()
            .responseBody
    }

    AccountBalancesDto exchangeMoneyExpectingSuccess(Long accountId, String exchangeRequest) {
        return patch(accountId, exchangeRequest)
            .expectStatus().isOk()
            .expectBody(AccountBalancesDto.class)
            .returnResult()
            .responseBody
    }

    ErrorResponse exchangeMoneyExpectingError(
        Long accountId,
        String exchangeRequest,
        HttpStatus expectedErrorStatus
    ) {
        return patch(accountId, exchangeRequest)
            .expectStatus().isEqualTo(expectedErrorStatus)
            .expectBody(ErrorResponse.class)
            .returnResult()
            .responseBody
    }

    AccountDetailsDto getAccountInfoExpectingSuccess(Long accountId) {
        return get(accountId)
            .expectStatus().isOk()
            .expectBody(AccountDetailsDto.class)
            .returnResult()
            .responseBody
    }

    ErrorResponse getAccountInfoExpectingError(
        Long accountId,
        HttpStatus expectedErrorStatus
    ) {
        return get(accountId)
            .expectStatus().isEqualTo(expectedErrorStatus)
            .expectBody(ErrorResponse.class)
            .returnResult()
            .responseBody
    }

    private WebTestClient.ResponseSpec post(String accountRequest) {
        webTestClient.post()
            .uri(uri -> uri
                .port(port)
                .path(CREATE_ACCOUNT_PATH)
                .build())
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(accountRequest)
            .exchange()
    }

    private WebTestClient.ResponseSpec patch(Long accountId, String exchangeRequest) {
        webTestClient.patch()
            .uri(uri -> uri
                .port(port)
                .path(EXCHANGE_PATH)
                .build(accountId))
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(exchangeRequest)
            .exchange()
    }

    private WebTestClient.ResponseSpec get(Long accountId) {
        webTestClient.get()
            .uri(uri -> uri
                .port(port)
                .path(ACCOUNT_INFO_PATH)
                .build(accountId))
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .exchange()
    }

}
