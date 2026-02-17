package org.example.event;
import java.time.LocalDateTime;
public abstract class AppEvent {
    private final Object source;
    private final LocalDateTime timestamp;
    public AppEvent(Object source) {
        this.source = source;
        this.timestamp = LocalDateTime.now();
    }
    public Object getSource() {
        return source;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{source=" + source + ", timestamp=" + timestamp + "}";
    }
}