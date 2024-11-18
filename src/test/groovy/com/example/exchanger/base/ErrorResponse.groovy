package com.example.exchanger.base

record ErrorResponse(
    String status,
    String message,
    String debugMessage
) {
}
