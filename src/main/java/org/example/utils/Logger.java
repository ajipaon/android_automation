package org.example.utils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.config.MongoConfig;
import org.example.config.Settings;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import java.time.LocalDateTime;
public class Logger {
    private static final String COLLECTION_NAME = "logs_activity";
    private static Logger instance;
    private final org.slf4j.Logger log;
    
    private static final Marker DB_MARKER = MarkerFactory.getMarker("DB");

    private Logger() {
        this.log = LoggerFactory.getLogger("AdbAutomation");
    }
    public static synchronized Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void debug(String message) {
        log.debug(message);
        if (Settings.LOG_TO_DB) saveToDb("DEBUG", message, null);
    }
    public void info(String message) {
        log.info(message);
        if (Settings.LOG_TO_DB) saveToDb("INFO", message, null);
    }
    public void warn(String message) {
        log.warn(message);
        if (Settings.LOG_TO_DB) saveToDb("WARN", message, null);
    }
    public void error(String message) {
        log.error(message);
        if (Settings.LOG_TO_DB) saveToDb("ERROR", message, null);
    }
    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
        if (Settings.LOG_TO_DB) saveToDb("ERROR", message, throwable);
    }

    public void debug(String format, Object... args) {
        debug(String.format(format, args));
    }
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }
    public void warn(String format, Object... args) {
        warn(String.format(format, args));
    }
    public void error(String format, Object... args) {
        error(String.format(format, args));
    }

    private void saveToDb(String level, String message, Throwable throwable) {
        try {
            MongoDatabase db = MongoConfig.getDatabase();
            MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
            Document doc = new Document()
                    .append("level", level)
                    .append("message", message)
                    .append("timestamp", LocalDateTime.now().toString())
                    .append("logger", "AdbAutomation");
            if (throwable != null) {
                doc.append("exception", throwable.getClass().getName())
                        .append("exception_message", throwable.getMessage());
            }
            collection.insertOne(doc);
        } catch (Exception e) {
            
            System.err.println("[Logger] Gagal menyimpan log ke MongoDB: " + e.getMessage());
        }
    }
}