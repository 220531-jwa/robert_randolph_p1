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
import dev.randolph.runner.EditRequestRunner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class EditRequestSteps {
    
    private WebDriver driver = EditRequestRunner.driver;
    private LoginPage loginPage = EditRequestRunner.loginPage;
    private HomePage homePage = EditRequestRunner.homePage;
    private RequestPage requestPage = EditRequestRunner.requestPage;
    
    // === EMPLOYEE EDITS ===
    
    @Given("The employee is logged in and on the request to edit")
    public void the_employee_is_logged_in_and_on_the_request_to_edit() {
        login("user1", "pass1");
        gotoRequest(3, false);
    }
    
    // Grade

    @When("The user enters a grade")
    public void the_user_enters_a_grade() {
        requestPage.inputGrade.sendKeys("P");
    }
    
    // Status

    @When("The user sets the status to cancelled")
    public void the_user_sets_the_status_to_cancelled() {
        requestPage.inputStatus.click();
        requestPage.inputStatus.findElement(By.xpath("./option[2]")).click();
    }
    
    // === MANAGER EDITS ===

    @Given("The manager is logged in and on the request to edit")
    public void the_manager_is_logged_in_and_on_the_request_to_edit() {
        login("admin1", "secret1");
        homePage.btnManage.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
        gotoRequest(4, true);
    }
    
    // Reimbursement amount

    @When("The manager sets the reimbursement amount")
    public void the_manager_sets_the_reimbursement_amount() {
        requestPage.inputReimAmount.clear();
        requestPage.inputReimAmount.sendKeys("50.00");
        
    }

    @When("The manager provides a reason for the change")
    public void the_manager_provides_a_reason_for_the_change() {
        requestPage.inputReason.clear();
        requestPage.inputReason.sendKeys("my awesome reason");
    }
    
    // Status

    @When("The manager sets the status to approved")
    public void the_manager_sets_the_status_to_approved() {
        requestPage.inputStatus.click();
        requestPage.inputStatus.findElement(By.xpath("./option[4]")).click();
    }
    
    // === BACK ===

    @Given("The employee is logged in and on the request page")
    public void the_employee_is_logged_in_and_on_the_request_page() {
        login("user1", "pass1");
        gotoRequest(3, false);
    }

    @When("The user clicks the back button")
    public void the_user_clicks_the_back_button() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click()", requestPage.btnBack);
//        requestPage.btnBack.click();    // Doesn't work for some reason
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleIs("Home Page"));
    }
    
    // === EVERYONE ===
    
    @When("The user clicks save")
    public void the_user_clicks_save() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click()", requestPage.btnSubmit);
//        requestPage.btnSubmit.click();  // Doesn't work for some reason
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleIs("Home Page"));
    }

    @Then("The user is on the homepage")
    public void the_user_is_on_the_homepage() {
        assertEquals("Home Page", driver.getTitle());
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
    
    public void gotoRequest(int id, boolean manager) {
        String path;
        
        if (manager) {
            path = "//*[@id=\"request_" + id + "\"]/td[2]/a";
        }
        else {
            path = "//*[@id=\"request_" + id + "\"]/td[1]/a";
        }
        
        driver.findElement(By.xpath(path)).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"inputStatus\"]/option"), 0));
    }
}
