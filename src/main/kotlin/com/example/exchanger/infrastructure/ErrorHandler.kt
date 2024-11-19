package com.example.exchanger.infrastructure

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JvmRecord
data class ErrorHolder(
    val status: HttpStatusCode?,
    val message: String?,
    val debugMessage: String?
) {
    class Builder {
        private var status: HttpStatus? = null
        private var message: String? = null
        private var debugMessage: String? = null

        fun status(status: HttpStatus?): Builder {
            this.status = status
            return this
        }

        fun message(message: String?): Builder {
            this.message = message
            return this
        }

        fun debugMessage(debugMessage: String?): Builder {
            this.debugMessage = debugMessage
            return this
        }

        fun build(): ErrorHolder {
            return ErrorHolder(status, message, debugMessage)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}