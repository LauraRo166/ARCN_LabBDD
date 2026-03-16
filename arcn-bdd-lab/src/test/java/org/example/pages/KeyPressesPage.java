package org.example.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class KeyPressesPage {
    private final WebDriver driver;

    @FindBy(id = "target")
    private WebElement inputField;

    @FindBy(id = "result")
    private WebElement resultText;

    public KeyPressesPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void navigate() {
        driver.get("https://the-internet.herokuapp.com/key_presses");
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('form').addEventListener('submit', function(e){ e.preventDefault(); });"
        );
    }

    public void pressKey(Keys key) {
        inputField.click();
        inputField.sendKeys(key);
    }

    public void pressKeyByChar(String character) {
        inputField.click();
        inputField.sendKeys(character);
    }

    public String getResult() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            wait.until(d -> {
                try {
                    String text = resultText.getText();
                    return text != null && !text.isBlank();
                } catch (StaleElementReferenceException e) {
                    PageFactory.initElements(driver, this);
                    return false;
                }
            });
        } catch (TimeoutException ignored) {
            // Return whatever is available so the assertion can report the mismatch.
        }

        return resultText.getText();
    }
}
