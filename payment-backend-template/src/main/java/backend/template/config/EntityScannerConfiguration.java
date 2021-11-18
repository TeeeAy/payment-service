package backend.template.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("backend.template")
public class EntityScannerConfiguration {
}
