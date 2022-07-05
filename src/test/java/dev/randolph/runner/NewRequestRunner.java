package dev.randolph.runner;

import org.junit.platform.suite.api.Suite;
import org.openqa.selenium.WebDriver;

import dev.randolph.page.HomePage;
import dev.randolph.page.LoginPage;
import dev.randolph.page.RequestPage;
import dev.randolph.util.ChromeDriverUtil;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

@Suite
public class NewRequestRunner {
    
    public static WebDriver driver;
    public static LoginPage loginPage;
    public static HomePage homePage;
    public static RequestPage requestPage;
    
    @BeforeAll
    public static void setup() {
        driver = ChromeDriverUtil.getChromeDriver();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        requestPage = new RequestPage(driver);
    }
    
    @AfterAll
    public static void teardown() {
        driver.quit();
    }
}
