package org.example.model.dto;
import org.bson.types.ObjectId;
import org.example.model.StatusJob;
import org.example.utils.AppFlow;
public class JodAddDto {
    private ObjectId id = new ObjectId();
    private StatusJob status = StatusJob.PENDING;
    private ObjectId deviceId;
    private AppFlow appFlow;
    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
    public StatusJob getStatus() {
        return status;
    }
    public void setStatus(StatusJob status) {
        this.status = status;
    }
    public ObjectId getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(ObjectId deviceId) {
        this.deviceId = deviceId;
    }
    public AppFlow getAppFlow() {
        return appFlow;
    }
    public void setAppFlow(AppFlow appFlow) {
        this.appFlow = appFlow;
    }
}
