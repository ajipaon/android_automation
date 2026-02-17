package org.example.tasks;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

public class LoginGmailExecutor {

    private static final String APPIUM_URL = "http://127.0.0.1:4723";

    public void runLoginGmail(String deviceId, String email, String password) throws Exception {

        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName(deviceId);
        options.setUdid(deviceId);
        options.setAppPackage("com.android.settings");
        options.setAppActivity(".Settings");
        options.setNoReset(true);
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        AndroidDriver driver = new AndroidDriver(new URL(APPIUM_URL), options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {

            System.out.println("[Gmail] Step 1: Buka menu Accounts");
            try {
                WebElement accountsMenu = wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath(
                                "//*[contains(@text,'Account') or contains(@content-desc,'Account')]"
                        ))
                );
                accountsMenu.click();
            } catch (Exception e) {
                // fallback: beberapa device pakai teks lowercase "account"
                WebElement altAccounts = wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath(
                                "//*[contains(translate(@text,'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                                        "'abcdefghijklmnopqrstuvwxyz'),'account')]"
                        ))
                );
                altAccounts.click();
            }

            System.out.println("[Gmail] Step 2: Klik Add account");
            WebElement addAccount = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(@text,'Add')]")
                    )
            );
            addAccount.click();

            System.out.println("[Gmail] Step 3: Pilih Google");
            WebElement googleItem = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@text='Google']")
                    )
            );
            googleItem.click();

            System.out.println("[Gmail] Step 4: Input email");
            WebElement emailField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.EditText")
                    )
            );
            emailField.sendKeys(email);
            WebElement nextBtn = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(@text,'Next')]")
                    )
            );
            nextBtn.click();

            System.out.println("[Gmail] Step 5: Input password");
            WebElement passwordField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//android.widget.EditText")
                    )
            );
            passwordField.sendKeys(password);
            WebElement nextBtn2 = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(@text,'Next')]")
                    )
            );
            nextBtn2.click();

            System.out.println("[Gmail] Step 6: Cek agreement...");
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(20));
                WebElement agreeBtn = shortWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//*[contains(@text,'I agree')]")
                        )
                );
                agreeBtn.click();
                System.out.println("[Gmail] Agreement diklik");
            } catch (Exception e) {
                System.out.println("[Gmail] Tidak ada agreement, lanjut...");
            }

            System.out.println("[Gmail] Step 7: Menunggu redirect...");
            Thread.sleep(10_000);
            System.out.println("[Gmail] Login selesai!");
        } finally {
            driver.quit();
            System.out.println("[Gmail] Driver quit");
        }
    }
}