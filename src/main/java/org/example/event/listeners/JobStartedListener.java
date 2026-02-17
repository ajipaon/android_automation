package org.example.event.listeners;
import org.example.event.EventListener;
import org.example.event.events.JobStartedEvent;
import org.example.model.StatusJob;
import org.example.service.DeviceService;
import org.example.service.JobService;
import org.example.utils.Logger;
public class JobStartedListener implements EventListener<JobStartedEvent> {
    private final Logger logger = Logger.getInstance();
    private final JobService jobService = new JobService();
    @Override
    public void onEvent(JobStartedEvent event) {
        logger.info("[JobStartedListener] Job dimulai → jobId: %s ",
                event.getJobId());
        
         jobService.updateJobStatus(event.getJobId(), StatusJob.RUNNING, null);
    }
    @Override
    public Class<JobStartedEvent> getEventType() {
        return JobStartedEvent.class;
    }
}