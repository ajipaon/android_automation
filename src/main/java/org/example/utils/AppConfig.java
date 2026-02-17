package org.example.utils;
import java.util.List;
public class AppConfig {
    private String appId, appName, appPackage, appActivity, udid, startUrl;
    private int waitTimeout;
    private List<StepConfig> steps;
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getStartUrl() { return startUrl; }
    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }
    public String getAppPackage() {
        return appPackage;
    }
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }
    public String getAppActivity() {
        return appActivity;
    }
    public void setAppActivity(String appActivity) {
        this.appActivity = appActivity;
    }
    public String getUdid() {
        return udid;
    }
    public void setUdid(String udid) {
        this.udid = udid;
    }
    public int getWaitTimeout() {
        return waitTimeout;
    }
    public void setWaitTimeout(int waitTimeout) {
        this.waitTimeout = waitTimeout;
    }
    public List<StepConfig> getSteps() {
        return steps;
    }
    public void setSteps(List<StepConfig> steps) {
        this.steps = steps;
    }
}
