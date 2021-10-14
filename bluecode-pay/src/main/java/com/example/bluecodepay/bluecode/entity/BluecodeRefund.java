package com.example.bluecodepay.bluecode.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder(setterPrefix = "with", toBuilder = true)
public class BluecodeRefund {

    private Integer amount;

    @JsonProperty("acquirer_tx_id")
    private String acquirerTxId;

}
