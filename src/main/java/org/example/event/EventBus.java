package org.example.event;
import org.example.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class EventBus {
    private static EventBus instance;
    
    private final Map<Class<?>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();
    
    private final ExecutorService executor;
    private final Logger logger = Logger.getInstance();

    private EventBus() {
        
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "EventBus-Worker");
            t.setDaemon(true); 
            return t;
        });
        logger.info("[EventBus] Initialized");
    }
    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public <T extends AppEvent> void register(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        logger.info("[EventBus] Listener registered → %s untuk event: %s",
                listener.getClass().getSimpleName(), eventType.getSimpleName());
    }
    public <T extends AppEvent> void unregister(EventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        List<EventListener<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
            logger.info("[EventBus] Listener unregistered → %s",
                    listener.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AppEvent> void publish(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            logger.debug("[EventBus] Tidak ada listener untuk event: %s",
                    event.getClass().getSimpleName());
            return;
        }
        logger.debug("[EventBus] Publishing event: %s → %d listener",
                event.getClass().getSimpleName(), eventListeners.size());
        for (EventListener<?> listener : eventListeners) {
            executor.submit(() -> {
                try {
                    ((EventListener<T>) listener).onEvent(event);
                } catch (Exception e) {
                    logger.error("[EventBus] Error di listener %s: %s",
                            listener.getClass().getSimpleName(), e.getMessage());
                }
            });
        }
    }
    @SuppressWarnings("unchecked")
    public <T extends AppEvent> void publishSync(T event) {
        List<EventListener<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            logger.debug("[EventBus] Tidak ada listener untuk event: %s",
                    event.getClass().getSimpleName());
            return;
        }
        logger.debug("[EventBus] Publishing sync event: %s → %d listener",
                event.getClass().getSimpleName(), eventListeners.size());
        for (EventListener<?> listener : eventListeners) {
            try {
                ((EventListener<T>) listener).onEvent(event);
            } catch (Exception e) {
                logger.error("[EventBus] Error di listener %s: %s",
                        listener.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    public void shutdown() {
        logger.info("[EventBus] Shutting down...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                logger.warn("[EventBus] Force shutdown setelah timeout");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("[EventBus] Shutdown selesai");
    }
}