package com.example.exchanger.exchangerate.infrastructure.nbp

class NbpResponse {

    public static String EXCHANGE_RATE = "4.0895"
    public static String PUBLISH_DATE = "2024-11-15"

    static String exchangeRateResponse(String rate = EXCHANGE_RATE, String publishDate = PUBLISH_DATE) {
        return """
            {
                "table": "A",
                "currency": "dolar amerykański",
                "code": "USD",
                "rates": [
                    {
                        "no": "222/A/NBP/2024",
                        "effectiveDate": "$publishDate",
                        "mid": $rate
                    }
                ]
            }
        """.stripIndent()
    }
}
