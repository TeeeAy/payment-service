package com.example.bluecodepay.bluecode.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with", toBuilder = true)
public class BluecodePaymentInfo {

    @JsonProperty("requested_amount")
    private Integer requestedAmount;

    @JsonProperty("acquirer_tx_id")
    private String acquirerTxId;

    private String code;

    @JsonProperty("merchant_tx_id")
    private String merchantTxId;

    private String currency;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("merchant_callback_url")
    private String merchantCallbackUrl;

    private String slip;

    private State state;

    @JsonProperty("end_to_end_id")
    private String endToEndId;
}
