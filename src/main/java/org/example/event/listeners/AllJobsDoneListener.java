package org.example.event.listeners;
import org.example.event.EventListener;
import org.example.event.events.AllJobsDoneEvent;
import org.example.utils.Logger;
public class AllJobsDoneListener implements EventListener<AllJobsDoneEvent> {
    private final Logger logger = Logger.getInstance();
    @Override
    public void onEvent(AllJobsDoneEvent event) {
        logger.info("[AllJobsDoneListener] Siklus selesai ✓ → total job: %d | waktu: %s",
                event.getTotalJobs(), event.getTimestamp());
        
    }
    @Override
    public Class<AllJobsDoneEvent> getEventType() {
        return AllJobsDoneEvent.class;
    }
}