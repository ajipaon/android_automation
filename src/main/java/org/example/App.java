package org.example;
import org.bson.types.ObjectId;
import org.example.api.ApiServer;
import org.example.config.AsyncConfig;
import org.example.config.MongoConfig;
import org.example.config.Settings;
import org.example.driver.AdbUtils;
import org.example.event.EventBus;
import org.example.event.events.DeviceCheckedEvent;
import org.example.event.events.JobPendingEvent;
import org.example.event.listeners.AllJobsDoneListener;
import org.example.event.listeners.DeviceCheckedListener;
import org.example.event.listeners.JobCompletedListener;
import org.example.event.listeners.JobFailedListener;
import org.example.event.listeners.JobPendingListener;
import org.example.event.listeners.JobStartedListener;
import org.example.model.Device;
import org.example.model.Job;
import org.example.model.dto.DeviceMinimalDto;
import org.example.service.DeviceService;
import org.example.service.JobService;
import org.example.utils.Logger;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class App {
    private static final Logger   logger   = Logger.getInstance();
    private static final EventBus eventBus = EventBus.getInstance();
    public static void main(String[] args) {
        logger.info("=== Android Automation Service Started ===");

        Settings.validate();

        ApiServer.start();

        registerListeners();

        JobService jobService = new JobService();
        DeviceService deviceService =  new DeviceService();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            logger.info("[Scheduler] ── Tick %s ──", java.time.LocalTime.now());
            try {
                List<DeviceMinimalDto> deviceMinimalDtoList = AdbUtils.getConnectedDevices();
                eventBus.publish(new DeviceCheckedEvent(App.class, deviceMinimalDtoList));
                if (deviceMinimalDtoList.isEmpty()) {
                    logger.warn("[Scheduler] Tidak ada device, skip.");
                    return;
                }
                List<Job> pendingJobs = jobService.getPendingJobs();
                if (pendingJobs.isEmpty()) {
                    logger.info("[Scheduler] Tidak ada job pending.");
                    return;
                }
                eventBus.publish(new JobPendingEvent(App.class, pendingJobs));
            } catch (Exception e) {
                logger.error("[Scheduler] Error: %s", e.getMessage());
            }
        }, 0, Settings.TASK_POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("[App] Shutting down...");
            scheduler.shutdown();
            ApiServer.stop();
            eventBus.shutdown();
            AsyncConfig.shutdown();
            MongoConfig.close();
            logger.info("[App] Bye!");
        }));
    }
    private static void registerListeners() {
        eventBus.register(new DeviceCheckedListener());
        eventBus.register(new JobPendingListener());
        eventBus.register(new JobStartedListener());
        eventBus.register(new JobCompletedListener());
        eventBus.register(new JobFailedListener());
        eventBus.register(new AllJobsDoneListener());
        logger.info("[App] Semua event listener terdaftar");
    }
}