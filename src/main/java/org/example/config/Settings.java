package org.example.config;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Settings {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .load();

    public static final String MONGO_URI =
            get("MONGO_URI", "");
    public static final String MONGO_DB_NAME =
            get("MONGO_DB_NAME", "adb_automation");

    public static final String ENCRYPTION_KEY =
            get("ENCRYPTION_KEY", "");

    public static final String ADB_PATH =
            get("ADB_PATH", "/usr/local/bin/adb");
    public static final int ADB_CONNECT_TIMEOUT =
            getInt("ADB_CONNECT_TIMEOUT", 30);
    public static final int ADB_EXECUTE_TIMEOUT =
            getInt("ADB_EXECUTE_TIMEOUT", 120);

    public static final int ACCOUNT_ROTATION_DAYS =
            getInt("ACCOUNT_ROTATION_DAYS", 7);
    public static final int TASK_POLL_INTERVAL_SECONDS =
            getInt("TASK_POLL_INTERVAL_SECONDS", 30);
    public static final int DEVICE_DISCOVERY_INTERVAL_SECONDS =
            getInt("DEVICE_DISCOVERY_INTERVAL_SECONDS", 30);

    public static final int MAX_ACTIVITY_DURATION_SECONDS =
            getInt("MAX_ACTIVITY_DURATION_SECONDS", 900);
    public static final int REVIEW_DELAY_MIN_SECONDS =
            getInt("REVIEW_DELAY_MIN_SECONDS", 10);
    public static final int REVIEW_DELAY_MAX_SECONDS =
            getInt("REVIEW_DELAY_MAX_SECONDS", 60);

    public static final String LOG_LEVEL =
            get("LOG_LEVEL", "INFO");
    public static final boolean LOG_TO_DB =
            getBoolean("LOG_TO_DB", true);
    public static final String LOG_FILE_PATH =
            get("LOG_FILE_PATH", "logs/adb_automation.log");

    public static final int MAX_RETRY_COUNT =
            getInt("MAX_RETRY_COUNT", 3);
    public static final int RETRY_DELAY_SECONDS =
            getInt("RETRY_DELAY_SECONDS", 5);

    public static final int MAX_CONCURRENT_DEVICES =
            getInt("MAX_CONCURRENT_DEVICES", 10);
    public static final int TASK_QUEUE_SIZE_LIMIT =
            getInt("TASK_QUEUE_SIZE_LIMIT", 100);

    public static boolean validate() {
        List<String> missingFields = new ArrayList<>();
        if (MONGO_URI.isEmpty())       missingFields.add("MONGO_URI");
        if (ENCRYPTION_KEY.isEmpty())  missingFields.add("ENCRYPTION_KEY");
        if (!missingFields.isEmpty()) {
            throw new IllegalStateException(
                    "Missing required environment variables: " + String.join(", ", missingFields)
            );
        }
        return true;
    }

    public static Map<String, Object> getSettingsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("MONGO_DB_NAME",                     MONGO_DB_NAME);
        map.put("ADB_PATH",                          ADB_PATH);
        map.put("ADB_CONNECT_TIMEOUT",               ADB_CONNECT_TIMEOUT);
        map.put("ACCOUNT_ROTATION_DAYS",             ACCOUNT_ROTATION_DAYS);
        map.put("TASK_POLL_INTERVAL_SECONDS",        TASK_POLL_INTERVAL_SECONDS);
        map.put("DEVICE_DISCOVERY_INTERVAL_SECONDS", DEVICE_DISCOVERY_INTERVAL_SECONDS);
        map.put("LOG_LEVEL",                         LOG_LEVEL);
        map.put("LOG_TO_DB",                         LOG_TO_DB);
        map.put("MAX_CONCURRENT_DEVICES",            MAX_CONCURRENT_DEVICES);
        return map;
    }

    private static String get(String key, String defaultValue) {
        String value = dotenv.get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    private static int getInt(String key, int defaultValue) {
        try {
            String value = dotenv.get(key);
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("[Settings] Invalid int value for key '" + key + "', using default: " + defaultValue);
            return defaultValue;
        }
    }
    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = dotenv.get(key);
        if (value == null || value.isEmpty()) return defaultValue;
        return value.trim().equalsIgnoreCase("true");
    }
}