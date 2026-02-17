package org.example.utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepConfig {
    private int stepId;
    private String action, description;
    private String locatorStrategy, locatorValue;
    private String inputText, expectedText, filename;
    private String scrollDirection, keyCode;
    private int startX, startY, endX, endY;
    private long durationMs;
    public int getStepId() {
        return stepId;
    }
    public void setStepId(int stepId) {
        this.stepId = stepId;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocatorStrategy() {
        return locatorStrategy;
    }
    public void setLocatorStrategy(String locatorStrategy) {
        this.locatorStrategy = locatorStrategy;
    }
    public String getLocatorValue() {
        return locatorValue;
    }
    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }
    public String getInputText() {
        return inputText;
    }
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }
    public String getExpectedText() {
        return expectedText;
    }
    public void setExpectedText(String expectedText) {
        this.expectedText = expectedText;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getScrollDirection() {
        return scrollDirection;
    }
    public void setScrollDirection(String scrollDirection) {
        this.scrollDirection = scrollDirection;
    }
    public String getKeyCode() {
        return keyCode;
    }
    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }
    public int getStartX() {
        return startX;
    }
    public void setStartX(int startX) {
        this.startX = startX;
    }
    public int getStartY() {
        return startY;
    }
    public void setStartY(int startY) {
        this.startY = startY;
    }
    public int getEndX() {
        return endX;
    }
    public void setEndX(int endX) {
        this.endX = endX;
    }
    public int getEndY() {
        return endY;
    }
    public void setEndY(int endY) {
        this.endY = endY;
    }
    public long getDurationMs() {
        return durationMs;
    }
    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
}
