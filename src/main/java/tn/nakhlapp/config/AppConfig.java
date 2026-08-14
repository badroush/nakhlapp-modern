package tn.nakhlapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException ignored) {
        }
    }

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        String envKey = key.replace('.', '_').toUpperCase();
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return PROPS.getProperty(key, defaultValue);
    }

    public static String dbHost() {
        return get("db.host", "localhost");
    }

    public static String dbPort() {
        return get("db.port", "3306");
    }
}
