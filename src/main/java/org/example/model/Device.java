package org.example.model;

import org.bson.types.ObjectId;

import java.time.LocalDate;

public class Device {
    private ObjectId id;
    private String deviceId;
    private String deviceName;
    private String model = "";
    private String androidVersion = "";
    private boolean internal = true;
    private ObjectId currentJobId;
    private StatusDevice statusDevice = StatusDevice.CONNECTED;
    private LocalDate lastSeen = LocalDate.now();
    private LocalDate createdAt;

    public Device() {
    }

    public Device(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public ObjectId getCurrentJobId() {
        return currentJobId;
    }

    public void setCurrentJobId(ObjectId currentJobId) {
        this.currentJobId = currentJobId;
    }

    public void setResetCurrentJobId() {
        this.currentJobId = null;
    }

    public StatusDevice getStatusDevice() {
        return statusDevice;
    }

    public void setStatusDevice(StatusDevice statusDevice) {
        this.statusDevice = statusDevice;
    }

    public LocalDate getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDate lastSeen) {
        this.lastSeen = lastSeen;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", model='" + model + '\'' +
                ", androidVersion='" + androidVersion + '\'' +
                ", currentJobId=" + currentJobId +
                ", statusDevice=" + statusDevice +
                ", lastSeen=" + lastSeen +
                ", createdAt=" + createdAt +
                '}';
    }
}