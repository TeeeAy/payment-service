package com.example.bluecodepay.bluecode.test.util;


import lombok.Builder;
import lombok.Data;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class BluecodeClientMocker {

    private BluecodeClientMocker() {

    }

    @Builder(setterPrefix = "with", toBuilder = true)
    @Data
    public static class MockInf{

        private String requestBody;

        private String responseBody;

        private int status;

        private String scenario;

        private String currentScenarioState;

        private String nextScenarioState;

    }

    public static void mockStartPayment(MockInf mockInf) throws IOException {

        givenThat(post(urlPathMatching("/payment"))
                .withRequestBody(equalToJson(FileReaderUtil.readFromFile(mockInf.getRequestBody())))
                .withHeader("Content-type", containing("application/json"))
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withStatus(mockInf.getStatus())
                        .withBody(FileReaderUtil.readFromFile(mockInf.getResponseBody()))));

    }

    public static void mockCancelPayment(MockInf mockInf) throws IOException {

        givenThat(post(urlPathMatching("/cancel"))
                .withRequestBody(equalToJson(FileReaderUtil.readFromFile(mockInf.getRequestBody())))
                .withHeader("Content-type", containing("application/json"))
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withStatus(mockInf.getStatus())
                        .withBody(FileReaderUtil.readFromFile(mockInf.getResponseBody()))));

    }


    public static void mockRefundPayment(MockInf mockInf) throws IOException {

        givenThat(post(urlPathMatching("/refund"))
                .withRequestBody(equalToJson(FileReaderUtil.readFromFile(mockInf.getRequestBody())))
                .withHeader("Content-type", containing("application/json"))
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withStatus(mockInf.getStatus())
                        .withBody(FileReaderUtil.readFromFile(mockInf.getResponseBody()))));

    }


    public static void mockStatusCheck(MockInf mockInf) throws IOException {
        givenThat(post(urlPathMatching("/status"))
                .withRequestBody(equalToJson(FileReaderUtil.readFromFile(mockInf.getRequestBody())))
                .withHeader("Content-type", containing("application/json"))
                .inScenario(mockInf.getScenario())
                .whenScenarioStateIs(mockInf.getCurrentScenarioState())
                .willReturn(aResponse()
                        .withHeader("Content-type", "application/json")
                        .withStatus(mockInf.getStatus())
                        .withBody(FileReaderUtil.readFromFile(mockInf.getResponseBody())))
                .willSetStateTo(mockInf.getNextScenarioState()));
    }


}
