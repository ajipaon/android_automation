package org.example.event;
public interface EventListener<T extends AppEvent> {
    void onEvent(T event);
    Class<T> getEventType();
}