package org.example.utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppFlow {
    private String flowId, flowName, description;
    private GlobalConfig globalConfig;
    private List<AppConfig> apps;
    public String getFlowId() {
        return flowId;
    }
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }
    public String getFlowName() {
        return flowName;
    }
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }
    public List<AppConfig> getApps() {
        return apps;
    }
    public void setApps(List<AppConfig> apps) {
        this.apps = apps;
    }

    public static class GlobalConfig {
        private boolean retryOnFailure;
        private int maxRetry;
        private boolean screenshotOnError;
        private long delayBetweenApps;
        public boolean isRetryOnFailure() {
            return retryOnFailure;
        }
        public void setRetryOnFailure(boolean retryOnFailure) {
            this.retryOnFailure = retryOnFailure;
        }
        public int getMaxRetry() {
            return maxRetry;
        }
        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }
        public boolean isScreenshotOnError() {
            return screenshotOnError;
        }
        public void setScreenshotOnError(boolean screenshotOnError) {
            this.screenshotOnError = screenshotOnError;
        }
        public long getDelayBetweenApps() {
            return delayBetweenApps;
        }
        public void setDelayBetweenApps(long delayBetweenApps) {
            this.delayBetweenApps = delayBetweenApps;
        }
    }
}
