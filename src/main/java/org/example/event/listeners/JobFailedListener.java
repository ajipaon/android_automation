package org.example.event.listeners;
import org.example.event.EventListener;
import org.example.event.events.JobFailedEvent;
import org.example.utils.Logger;
public class JobFailedListener implements EventListener<JobFailedEvent> {
    private final Logger logger = Logger.getInstance();
    @Override
    public void onEvent(JobFailedEvent event) {
        logger.error("[JobFailedListener] Job gagal → jobId: %s | error: %s",
                event.getJobId(), event.getErrorMessage());

    }
    @Override
    public Class<JobFailedEvent> getEventType() {
        return JobFailedEvent.class;
    }
}