package com.example.bluecodepay.bluecode.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WireMockConfig implements
        BeforeAllCallback, AfterAllCallback {

    public static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        WIRE_MOCK_SERVER.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        WIRE_MOCK_SERVER.stop();
    }
}
