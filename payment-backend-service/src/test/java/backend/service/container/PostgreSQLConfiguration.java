package backend.service.container;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PostgreSQLConfiguration implements
        BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        Postgres.CONTAINER.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        Postgres.CONTAINER.close();
    }
}
