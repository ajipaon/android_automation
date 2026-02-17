package org.example.driver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URL;
public class AppiumDriverManager {

    private static final String APPIUM_URL = "http://127.0.0.1:4723";

    public static AndroidDriver createDriver(String udid, String appPackage, String appActivity) throws Exception {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setUdid(udid);
        options.setAppPackage(appPackage);
        options.setAppActivity(appActivity);
        options.setNoReset(true);
        options.setSystemPort(8200);
        AndroidDriver driver = new AndroidDriver(new URL(APPIUM_URL), options);
        System.out.println("[Appium] Driver created → session: " + driver.getSessionId());
        return driver;
    }
    public static void quitDriver(AndroidDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("[Appium] Driver quit");
            } catch (Exception e) {
                System.out.println("[Appium] Error saat quit: " + e.getMessage());
            }
        }
    }
}