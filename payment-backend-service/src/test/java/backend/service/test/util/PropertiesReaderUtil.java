package backend.service.test.util;

import lombok.SneakyThrows;
import org.apache.logging.log4j.util.PropertiesUtil;

import java.util.Properties;

public class PropertiesReaderUtil {

    private static final Properties PROPERTIES = createPropertiesMap();

    private PropertiesReaderUtil() {
    }

    public static String getProperty(String name) {
        return PROPERTIES.getProperty(name);
    }

    @SneakyThrows
    private static Properties createPropertiesMap() {
        final Properties properties = new Properties();
        properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("application-test.properties"));
        return properties;
    }



}
