package org.example.event.listeners;
import org.example.event.EventBus;
import org.example.event.EventListener;
import org.example.event.events.AllJobsDoneEvent;
import org.example.event.events.JobCompletedEvent;
import org.example.event.events.JobFailedEvent;
import org.example.event.events.JobPendingEvent;
import org.example.event.events.JobStartedEvent;
import org.example.model.Job;
import org.example.service.JobExecutor;
import org.example.utils.Logger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class JobPendingListener implements EventListener<JobPendingEvent> {
    private final Logger      logger      = Logger.getInstance();
    private final EventBus    eventBus    = EventBus.getInstance();
    private final JobExecutor jobExecutor = new JobExecutor();
    @Override
    public void onEvent(JobPendingEvent event) {
        List<Job> jobs = event.getPendingJobs();
        logger.info("[JobPendingListener] Memproses %d job ", jobs.size());
        
        AtomicInteger remaining = new AtomicInteger(jobs.size());
        for (Job job : jobs) {
            
            eventBus.publish(new JobStartedEvent(this, job.getId()));

            jobExecutor.executeAsync(job)
                    .thenRun(() -> {
                        
                        eventBus.publish(new JobCompletedEvent(this, job.getId()));
                        checkAllDone(remaining, jobs.size());
                    })
                    .exceptionally(ex -> {
                        
                        eventBus.publish(new JobFailedEvent(
                                this,
                                job.getId(),
                                ex.getMessage()
                        ));
                        checkAllDone(remaining, jobs.size());
                        return null;
                    });
        }
    }
    private void checkAllDone(AtomicInteger remaining, int total) {
        if (remaining.decrementAndGet() == 0) {
            logger.info("[JobPendingListener] Semua job selesai ✓");
            eventBus.publish(new AllJobsDoneEvent(this, total));
        }
    }
    @Override
    public Class<JobPendingEvent> getEventType() {
        return JobPendingEvent.class;
    }
}