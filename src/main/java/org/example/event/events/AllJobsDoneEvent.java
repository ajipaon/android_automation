package org.example.event.events;
import org.example.event.AppEvent;
public class AllJobsDoneEvent extends AppEvent {
    private final int totalJobs;
    public AllJobsDoneEvent(Object source, int totalJobs) {
        super(source);
        this.totalJobs = totalJobs;
    }
    public int getTotalJobs() {
        return totalJobs;
    }
}