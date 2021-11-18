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
@Builder(setterPrefix = "with", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BluecodePaymentStatus {

    @JsonProperty("merchant_tx_id")
    private String merchantTxId;

    private Integer ttl;

    @JsonProperty("check_status_in")
    private Integer checkStatusIn;
}
