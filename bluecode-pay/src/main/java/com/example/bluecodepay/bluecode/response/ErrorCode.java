package com.example.bluecodepay.bluecode.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SYSTEM_FAILURE("Unexpected technical error. Please try again. Contact support if the problem persists.",
            503),
    SERVICE_UNAVAILABLE("Blue Code is currently unable to process transactions. Please try again.",
            503),
    ISSUER_FAILURE("The customer\\'s Blue Code account is not supported.",
            400),
    INVALID_BARCODE("Invalid barcode. Ask the customer to generate a new code and try again.",
            400),
    BRANCH_NOT_FOUND("Wrong configuration (invalid branch ID). Please contact an administrator.",
            500),
    INVALID_PARAMETER("Fatal technical error (\"invalid parameter\"). Please contact support.",
            500),
    REFUND_NOT_SUPPORTED("Refund of this payment is not supported.",
            500),
    FRAUD_DETECTED("Payment was declined. Use a different form of payment.", 200),
    INSUFFICIENT_FUNDS("Payment was declined. Use a different form of payment.", 200),
    INVALID_STATE("Payment was declined. Use a different form of payment.", 200),
    LIMIT_EXCEEDED("Payment was declined. Use a different form of payment.", 200),
    CANCELLED_BY_USER("The customer canceled the payment in the app.",200),
    CANCEL_PERIOD_EXPIRED("The cancellation period of this payment is expired",200),
    TRANSACTION_NOT_FOUND("Unknown transaction. Could not refund.", 404),
    UNAUTHORIZED("Wrong configuration (username or password is invalid). Please contact an administrator.",
            401),
    MERCHANT_TX_ID_NOT_UNIQUE("Internal error (\"transaction ID not unique\"). " +
            "Please try again. Contact support if the problem persists.", 500);

    private final String errorMessage;

    private final int statusCode;

    ErrorCode(String errorMessage, int statusCode){
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

}
