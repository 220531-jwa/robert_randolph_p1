package dev.randolph.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.randolph.page.HomePage;
import dev.randolph.page.LoginPage;
import dev.randolph.runner.LoginRunner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {
    
    private WebDriver driver = LoginRunner.driver;
    private LoginPage loginPage = LoginRunner.loginPage;
    private HomePage homePage = LoginRunner.homePage;
    
    // === LOGIN ===
    
    @Given("The user is on the login page")
    public void the_user_is_on_the_login_page() {
        driver.get("http://localhost:8080/html/loginPage.html");
    }

    @When("The user types in their {string} and {string} and click the signin button")
    public void the_user_types_in_their_and(String username, String password) {
        loginPage.inputUsername.sendKeys(username);
        loginPage.inputPassword.sendKeys(password);
        loginPage.signInBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleIs("Home Page"));
    
    }

    @Then("The user will be on the homepage")
    public void the_user_will_be_on_the_homepage() {
        assertEquals("Home Page", driver.getTitle());
    }
    
    // === LOGOUT ===
    
    @Given("The user is logged in and on the homepage")
    public void the_user_is_logged_in_and_on_the_homepage() {
        login("user1", "pass1");
    }

    @When("The user clicks the logout button")
    public void the_user_clicks_the_logout_button() {
        homePage.btnLogout.click();
    }

    @Then("The user will be on the logout page")
    public void the_user_will_be_on_the_logout_page() {
        assertEquals("Login Page", driver.getTitle());
    }
    
    // === UTILITY ===
    
    public void login(String username, String password) {
        driver.get("http://localhost:8080/html/loginPage.html");
        loginPage.inputUsername.sendKeys(username);
        loginPage.inputPassword.sendKeys(password);
        loginPage.signInBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleIs("Home Page"));
    }
}
