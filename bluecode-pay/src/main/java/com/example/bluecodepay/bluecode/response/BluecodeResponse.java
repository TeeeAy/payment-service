package com.example.bluecodepay.bluecode.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder("type")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(setterPrefix = "with", toBuilder = true)
public class BluecodeResponse {

   private Result result;

   private BluecodePaymentInfo payment;

   private BluecodePaymentStatus status;

   @JsonProperty("error_code")
   private ErrorCode errorCode;


}
