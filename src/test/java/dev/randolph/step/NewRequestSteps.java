package dev.randolph.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.randolph.page.HomePage;
import dev.randolph.page.LoginPage;
import dev.randolph.page.RequestPage;
import dev.randolph.runner.NewRequestRunner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NewRequestSteps {
    
    private WebDriver driver = NewRequestRunner.driver;
    private LoginPage loginPage = NewRequestRunner.loginPage;
    private HomePage homePage = NewRequestRunner.homePage;
    private RequestPage requestPage = NewRequestRunner.requestPage;
    
    @Given("The user is logged in and on the home page with your requests")
    public void the_user_is_logged_in_and_on_the_home_page_with_your_requests() {
        login("admin1", "secret1");
    }
    
    @When("The user clicks on the New Request button")
    public void the_user_clicks_on_the_new_request_button() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.elementToBeClickable(homePage.btnNewRequest));
        homePage.btnNewRequest.click();
    }

    @When("The user enters the required fields")
    public void the_user_enters_the_required_fields() {
        requestPage.inputEventType.click();
        requestPage.inputEventType.findElement(By.xpath("./option[2]")).click();
        requestPage.inputCost.sendKeys("100.00");
        requestPage.inputGradeFormat.click();
        requestPage.inputGradeFormat.findElement(By.xpath("./option[2]")).click();
        requestPage.inputCutoff.sendKeys("C");
        requestPage.inputDescription.sendKeys("desc");
        requestPage.inputLocation.sendKeys("loc");
        requestPage.inputStartDate.sendKeys("07042022");
        requestPage.inputStartTime.sendKeys("11111");
        requestPage.inputJustification.sendKeys("just");
    }

    @When("The user clicks the submit button")
    public void the_user_clicks_the_submit_button() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click()", requestPage.btnSubmit);
//        requestPage.btnSubmit.click();  // Doesn't work for some reason
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
    }

    @Then("The user will be on the home page")
    public void the_user_will_be_on_the_home_page() {
        assertEquals("Home Page", driver.getTitle());
    }

    @Then("A new request will be in the table")
    public void a_new_request_will_be_in_the_table() {
        assertEquals(true, homePage.tableItems.findElements(By.xpath("./tr")).size() == 4);
    }
    
    // === UTILITY ===
    
    public void login(String username, String password) {
        driver.get("http://localhost:8080/html/loginPage.html");
        loginPage.inputUsername.sendKeys(username);
        loginPage.inputPassword.sendKeys(password);
        loginPage.signInBtn.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
    }
}
