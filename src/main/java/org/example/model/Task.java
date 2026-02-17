package org.example.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private ObjectId id;
    private String taskType;
    private String appName;
    private String appPackage;
    private String appUrl;
    private String testingUrl;
    private List<String> activities = new ArrayList<>();
    private List<String> testersEmails = new ArrayList<>();
    private int priority;
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private int retryCount = 0;
    private String errorMessage;
    private LocalDateTime failedAt;
    private ObjectId assignedAccount;
    private ObjectId assignedDevice;

    public Task() {
    }

    public Task(String taskType, String appName, String appPackage) {
        this.taskType = taskType;
        this.appName = appName;
        this.appPackage = appPackage;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getTestingUrl() {
        return testingUrl;
    }

    public void setTestingUrl(String testingUrl) {
        this.testingUrl = testingUrl;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public List<String> getTestersEmails() {
        return testersEmails;
    }

    public void setTestersEmails(List<String> testersEmails) {
        this.testersEmails = testersEmails;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public ObjectId getAssignedAccount() {
        return assignedAccount;
    }

    public void setAssignedAccount(ObjectId assignedAccount) {
        this.assignedAccount = assignedAccount;
    }

    public ObjectId getAssignedDevice() {
        return assignedDevice;
    }

    public void setAssignedDevice(ObjectId assignedDevice) {
        this.assignedDevice = assignedDevice;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskType='" + taskType + '\'' +
                ", appName='" + appName + '\'' +
                ", appPackage='" + appPackage + '\'' +
                ", appUrl='" + appUrl + '\'' +
                ", testingUrl='" + testingUrl + '\'' +
                ", activities=" + activities +
                ", testersEmails=" + testersEmails +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", retryCount=" + retryCount +
                ", errorMessage='" + errorMessage + '\'' +
                ", failedAt=" + failedAt +
                ", assignedAccount=" + assignedAccount +
                ", assignedDevice=" + assignedDevice +
                '}';
    }
}