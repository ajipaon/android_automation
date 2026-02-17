package org.example.event.events;
import org.example.event.AppEvent;
import org.example.model.Job;
import java.util.List;
public class JobPendingEvent extends AppEvent {
    private final List<Job> pendingJobs;
    public JobPendingEvent(Object source, List<Job> pendingJobs) {
        super(source);
        this.pendingJobs = pendingJobs;
    }
    public List<Job> getPendingJobs() {
        return pendingJobs;
    }
}