package org.example.event.events;
import org.bson.types.ObjectId;
import org.example.event.AppEvent;
public class JobCompletedEvent extends AppEvent {
    private final ObjectId jobId;
    public JobCompletedEvent(Object source, ObjectId jobId ) {
        super(source);
        this.jobId = jobId;
    }
    public ObjectId getJobId() {
        return jobId;
    }
}