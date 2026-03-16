package org.example.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.KeyPressesPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class KeyPressesSteps {
    private static WebDriver sharedDriver;
    private static boolean shutdownHookRegistered;
    private KeyPressesPage keyPressesPage;

    @Before("@keypresses")
    public void setUpScenario() {
        if (sharedDriver == null) {
            sharedDriver = createDriverWithRetry();
            registerShutdownHook();
        }
        keyPressesPage = new KeyPressesPage(sharedDriver);
    }

    @Given("the user is on the Key Presses page")
    public void the_user_is_on_the_key_presses_page() {
        keyPressesPage.navigate();
    }

    @When("the user presses the {string} key")
    public void the_user_presses_the_key(String key) {
        String normalizedKey = key.trim().toUpperCase();

        try {
            Keys seleniumKey;
            switch (normalizedKey) {
                case "UP":
                    seleniumKey = Keys.ARROW_UP;
                    break;
                case "DOWN":
                    seleniumKey = Keys.ARROW_DOWN;
                    break;
                case "LEFT":
                    seleniumKey = Keys.ARROW_LEFT;
                    break;
                case "RIGHT":
                    seleniumKey = Keys.ARROW_RIGHT;
                    break;
                default:
                    seleniumKey = Keys.valueOf(normalizedKey);
                    break;
            }
            keyPressesPage.pressKey(seleniumKey);
        } catch (IllegalArgumentException e) {
            keyPressesPage.pressKeyByChar(key);
        }
    }

    @Then("the result should show {string}")
    public void the_result_should_show(String expectedResult) {
        assertEquals(expectedResult, keyPressesPage.getResult());
    }

    private static WebDriver createDriverWithRetry() {
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
                ChromeOptions options = new ChromeOptions();
                Path chromeBinary = Path.of("/usr/local/bin/google-chrome");
                if (Files.isExecutable(chromeBinary)) {
                    options.setBinary(chromeBinary.toString());
                }

                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--remote-allow-origins=*");

                WebDriver driver = new ChromeDriver(options);
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                return driver;
            } catch (Exception e) {
                lastError = new RuntimeException("Failed to initialize ChromeDriver (attempt " + attempt + ")", e);
                try {
                    Thread.sleep(750L);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while retrying ChromeDriver initialization", interruptedException);
                }
            }
        }

        throw new RuntimeException("Failed to initialize ChromeDriver", lastError);
    }

    private static synchronized void registerShutdownHook() {
        if (shutdownHookRegistered) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (sharedDriver != null) {
                sharedDriver.quit();
                sharedDriver = null;
            }
        }));

        shutdownHookRegistered = true;
    }
}
