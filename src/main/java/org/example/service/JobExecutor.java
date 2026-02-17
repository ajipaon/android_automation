package org.example.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.bson.types.ObjectId;
import org.example.config.AsyncConfig;
import org.example.driver.AppiumDriverManager;
import org.example.model.Device;
import org.example.model.Job;
import org.example.model.StatusJob;
import org.example.utils.AppConfig;
import org.example.utils.AppFlow;
import org.example.utils.StepConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public class JobExecutor {
    private final JobService jobService = new JobService();
    private final ObjectMapper mapper = new ObjectMapper();
    private final DeviceService deviceService = new DeviceService();
    
    public void executeFlow(Job job) {
        jobService.updateJobStatus(job.getId(), StatusJob.RUNNING, null);
        Device device = deviceService.getDeviceById(new ObjectId(job.getDeviceId()));
        try {
            String flowJson = job.getDataExecution();
            if(device.getCurrentJobId() != null){
                throw new Exception("device on job");
            }
            if (flowJson == null || flowJson.isBlank()) {
                throw new Exception("dataExecution kosong, tidak ada flow JSON.");
            }
            
            AppFlow flow = mapper.readValue(flowJson, AppFlow.class);
            System.out.println("[Flow] Memulai: " + flow.getFlowName());
            device.setCurrentJobId(job.getId());
            deviceService.updateDevice(device);
            for (AppConfig app : flow.getApps()) {
                System.out.println("[Flow] >> App: " + app.getAppName());
                app.setUdid(device.getDeviceId());
                boolean success = executeApp(app, flow.getGlobalConfig());
                if (!success && !flow.getGlobalConfig().isRetryOnFailure()) {
                    throw new Exception("App gagal dan retryOnFailure=false: " + app.getAppName());
                }
                Thread.sleep(flow.getGlobalConfig().getDelayBetweenApps());
            }
            jobService.updateJobStatus(job.getId(), StatusJob.FINISHED, null);
            System.out.println("[Flow] Semua app selesai.");
        } catch (Exception e) {
            System.err.println("[Flow] Error: " + e.getMessage());
            jobService.updateJobStatus(job.getId(), StatusJob.PENDING, e.getMessage());
        }finally {
            device.setResetCurrentJobId();
            deviceService.updateDevice(device);
        }
    }
    
    public CompletableFuture<Void> executeAsync(Job job) {
        return CompletableFuture
                .runAsync(() -> executeFlow(job), AsyncConfig.getExecutor())
                .thenRun(() ->
                        System.out.println("[Async] ✓ Job done  : " + job.getId()
                                + " | thread: " + Thread.currentThread().getName())
                )
                .exceptionally(ex -> {
                    System.out.println("[Async] ✗ Job failed: " + job.getId()
                            + " | error: " + ex.getMessage());
                    return null;
                });
    }
    
    private boolean executeApp(AppConfig app, AppFlow.GlobalConfig global) {
        AndroidDriver driver = null;
        int attempt = 0;
        int maxAttempts = global.isRetryOnFailure() ? global.getMaxRetry() + 1 : 1;
        while (attempt < maxAttempts) {
            attempt++;
            try {
                driver = AppiumDriverManager.createDriver(
                        app.getUdid(),
                        app.getAppPackage(),
                        app.getAppActivity()
                );
                if (driver.isDeviceLocked()) {
                    System.out.println("[App] Device terkunci, melakukan unlock...");
                    driver.unlockDevice();
                    Thread.sleep(1500);
                }
                if (app.getStartUrl() != null && !app.getStartUrl().isBlank()) {
                    System.out.println("[App] Navigasi ke URL: " + app.getStartUrl());
                    driver.get(app.getStartUrl());
                }
                for (StepConfig step : app.getSteps()) {
                    System.out.printf("[Step %d] %s%n", step.getStepId(),
                            step.getDescription() != null ? step.getDescription() : step.getAction());
                    executeStep(driver, step, app);
                }
                return true;
            } catch (Exception e) {
                System.err.printf("[App:%s] Attempt %d gagal: %s%n",
                        app.getAppName(), attempt, e.getMessage());
                if (global.isScreenshotOnError() && driver != null) {
                    try {
                        doScreenshot(driver, app.getAppId() + "_error_attempt" + attempt);
                    } catch (Exception ignored) {}
                }
            } finally {
                AppiumDriverManager.quitDriver(driver);
                driver = null;
            }
        }
        return false;
    }
    
    private void executeStep(AndroidDriver driver, StepConfig step, AppConfig app) throws Exception {
        switch (step.getAction()) {
            case "open_app" -> {
                System.out.println("[Step] App " + app.getAppName() + " sudah dibuka.");
            }
            case "wait" -> {
                long ms = step.getDurationMs() > 0 ? step.getDurationMs() : 1000;
                Thread.sleep(ms);
            }
            case "click" -> {
                findElement(driver, step, app.getWaitTimeout()).click();
            }
            case "input" -> {
                WebElement el = findElement(driver, step, app.getWaitTimeout());
                el.sendKeys(step.getInputText());
            }
            case "unlock_device" -> {
                if (driver.isDeviceLocked()) {
                    driver.unlockDevice();
                    System.out.println("[Step] Device berhasil di-unlock.");
                } else {
                    System.out.println("[Step] Device sudah dalam keadaan unlocked.");
                }
                Thread.sleep(1000);
            }
            case "clear_and_input" -> {
                WebElement el = findElement(driver, step, app.getWaitTimeout());
                el.clear();
                el.sendKeys(step.getInputText());
            }
            case "scroll" -> {
                doScroll(driver, step.getScrollDirection());
            }
            case "swipe" -> {
                doSwipe(driver,
                        step.getStartX(), step.getStartY(),
                        step.getEndX(), step.getEndY(),
                        step.getDurationMs()
                );
            }
            case "long_press" -> {
                WebElement el = findElement(driver, step, app.getWaitTimeout());
                doLongPress(driver, el, step.getDurationMs());
            }
            case "key_press" -> {
                doKeyPress(driver, step.getKeyCode());
            }
            case "screenshot" -> {
                String fname = step.getFilename() != null
                        ? step.getFilename()
                        : app.getAppId() + "_step" + step.getStepId();
                doScreenshot(driver, fname);
            }
            case "assert_text" -> {
                WebElement el = findElement(driver, step, app.getWaitTimeout());
                String actual = el.getText().trim();
                String expected = step.getExpectedText();
                if (!actual.equals(expected)) {
                    throw new AssertionError(
                            String.format("Assert gagal! Expected='%s' Actual='%s'", expected, actual));
                }
                System.out.printf("[Assert] OK: '%s' == '%s'%n", actual, expected);
            }
            default -> throw new Exception("Action tidak dikenal: " + step.getAction());
        }
    }
    
    private WebElement findElement(AndroidDriver driver, StepConfig step, int timeoutSec) {
        long deadline = System.currentTimeMillis() + (timeoutSec * 1000L);
        
        String[] locatorValues = step.getLocatorValue().split("\\s*\\|\\s*");
        while (System.currentTimeMillis() < deadline) {
            for (String locatorValue : locatorValues) {
                try {
                    By locator = buildLocator(step.getLocatorStrategy(), locatorValue.trim());
                    List<WebElement> els = driver.findElements(locator);
                    if (!els.isEmpty()) {
                        System.out.println("[Locator] Ditemukan dengan: " + locatorValue.trim());
                        return els.get(0);
                    }
                } catch (Exception ignored) {}
            }
            try { Thread.sleep(500); } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Element tidak ditemukan: " + step.getLocatorValue());
    }
    
    private By buildLocator(String strategy, String value) {
        return switch (strategy) {
            case "id"               -> By.id(value);
            case "xpath"            -> By.xpath(value);
            case "accessibility_id" -> AppiumBy.accessibilityId(value);
            case "class_name"       -> By.className(value);
            case "text"             -> By.xpath("//*[@text='" + value + "']");
            case "uiautomator"      -> AppiumBy.androidUIAutomator(value);
            default                 -> By.id(value);
        };
    }
    private void doScroll(AndroidDriver driver, String direction) {
        var size = driver.manage().window().getSize();
        int cx     = size.width / 2;
        int startY = direction.equals("down") ? (int)(size.height * 0.7) : (int)(size.height * 0.3);
        int endY   = direction.equals("down") ? (int)(size.height * 0.3) : (int)(size.height * 0.7);
        doSwipe(driver, cx, startY, cx, endY, 600);
    }
    private void doSwipe(AndroidDriver driver,
                         int startX, int startY, int endX, int endY, long durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(
                Duration.ofMillis(durationMs > 0 ? durationMs : 500),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(swipe));
    }
    private void doLongPress(AndroidDriver driver, WebElement element, long durationMs) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        var loc = element.getLocation();
        var sz  = element.getSize();
        int cx  = loc.getX() + sz.getWidth() / 2;
        int cy  = loc.getY() + sz.getHeight() / 2;
        Sequence press = new Sequence(finger, 0);
        press.addAction(finger.createPointerMove(Duration.ZERO,
                PointerInput.Origin.viewport(), cx, cy));
        press.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        press.addAction(finger.createPointerMove(
                Duration.ofMillis(durationMs > 0 ? durationMs : 2000),
                PointerInput.Origin.viewport(), cx, cy));
        press.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(press));
    }
    private void doKeyPress(AndroidDriver driver, String keyCode) throws Exception {
        if (keyCode == null) throw new Exception("keyCode tidak boleh null");
        switch (keyCode.toUpperCase()) {
            case "ENTER"  -> driver.executeScript("mobile: pressKey", java.util.Map.of("keycode", 66));
            case "BACK"   -> driver.executeScript("mobile: pressKey", java.util.Map.of("keycode", 4));
            case "HOME"   -> driver.executeScript("mobile: pressKey", java.util.Map.of("keycode", 3));
            case "DELETE" -> driver.executeScript("mobile: pressKey", java.util.Map.of("keycode", 67));
            default       -> throw new Exception("keyCode tidak dikenal: " + keyCode);
        }
    }
    private void doScreenshot(AndroidDriver driver, String filename) throws Exception {
        File src  = driver.getScreenshotAs(OutputType.FILE);
        File dest = new File("screenshots/" + filename + "_" + System.currentTimeMillis() + ".png");
        dest.getParentFile().mkdirs();
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[Screenshot] Disimpan: " + dest.getAbsolutePath());
    }
}