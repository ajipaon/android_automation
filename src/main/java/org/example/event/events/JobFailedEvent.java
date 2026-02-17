package org.example.event.events;
import org.bson.types.ObjectId;
import org.example.event.AppEvent;
public class JobFailedEvent extends AppEvent {
    private final ObjectId jobId;
    private final String errorMessage;
    public JobFailedEvent(Object source, ObjectId jobId, String errorMessage) {
        super(source);
        this.jobId        = jobId;
        this.errorMessage = errorMessage;
    }
    public ObjectId getJobId() {
        return jobId;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}