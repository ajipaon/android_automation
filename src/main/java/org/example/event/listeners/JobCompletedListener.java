package org.example.event.listeners;
import org.example.event.EventListener;
import org.example.event.events.JobCompletedEvent;
import org.example.utils.Logger;
public class JobCompletedListener implements EventListener<JobCompletedEvent> {
    private final Logger logger = Logger.getInstance();
    @Override
    public void onEvent(JobCompletedEvent event) {
        logger.info("[JobCompletedListener] Job selesai → jobId: %s ",
                event.getJobId());

    }
    @Override
    public Class<JobCompletedEvent> getEventType() {
        return JobCompletedEvent.class;
    }
}