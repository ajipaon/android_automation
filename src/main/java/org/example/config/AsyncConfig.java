package org.example.config;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class AsyncConfig {
    private static final int THREAD_POOL_SIZE = 3;
    private static final ExecutorService executor =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    public static ExecutorService getExecutor() {
        return executor;
    }
    public static void shutdown() {
        System.out.println("[AsyncConfig] Shutting down thread pool...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        System.out.println("[AsyncConfig] Thread pool stopped");
    }
}