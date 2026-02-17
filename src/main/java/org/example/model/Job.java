package org.example.model;
import com.google.j2objc.annotations.OnDealloc;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.Date;
public class Job {
    private ObjectId id = new ObjectId();
    private StatusJob status = StatusJob.PENDING;    
    private LocalDate createdAt = LocalDate.now();
    private ObjectId deviceId;
    private String dataExecution = "";
    
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public StatusJob getStatus() { return status; }
    public void setStatus(StatusJob status) { this.status = status; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public String getDeviceId(){
        return deviceId.toHexString();
    }
    public void setDeviceId(ObjectId deviceId){
        this.deviceId = deviceId;
    }
    public String getDataExecution(){
        return dataExecution;
    }
    public void setDataExecution(String data){
        this.dataExecution = data;
    }
}