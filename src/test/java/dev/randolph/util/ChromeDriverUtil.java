package dev.randolph.util;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeDriverUtil {
    
    private static ChromeDriverUtil chromeDriverSetup;
    
    private ChromeDriverUtil() {
        File chrome = new File("src/test/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", chrome.getAbsolutePath());
    }
    
    public static WebDriver getChromeDriver() {
        if (chromeDriverSetup == null) {
            chromeDriverSetup = new ChromeDriverUtil();
        }
        return new ChromeDriver();
    }
}
