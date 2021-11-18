package com.example.bluecodepay.bluecode.entity;


import backend.template.entity.Payment;
import backend.template.entity.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@SuppressWarnings({"LombokEqualsAndHashCodeInspection", "LombokDataInspection"})
@NoArgsConstructor
@Data
@Entity
@Table
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder(setterPrefix = "with", toBuilder = true)
@JsonPropertyOrder({"merchantTxId"})
@JsonIgnoreProperties("status")
public class BluecodePayment extends Payment {

    private static final String DEFAULT_CURRENCY = "EUR";

    private static final String BRANCH_TEXT_ID = "test";

    @JsonProperty("branch_ext_id")
    @Transient
    @Builder.Default
    private String branchTextId = BRANCH_TEXT_ID;

    @Column
    @JsonIgnore
    private String acquirerTxId;

    @Column
    private String barcode;

    @Column
    @JsonProperty("requested_amount")
    private Integer requestedAmount;

    @Column
    @Builder.Default
    private String currency = DEFAULT_CURRENCY;

    @JsonProperty("merchant_tx_id")
    public String getMerchantTxId() {
        return super.getId();
    }

    @JsonIgnore
    public String getType() {
        return "bluecode";
    }

}
