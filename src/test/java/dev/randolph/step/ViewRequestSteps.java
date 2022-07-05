package dev.randolph.step;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.randolph.model.enums.RequestStatus;
import dev.randolph.page.HomePage;
import dev.randolph.page.LoginPage;
import dev.randolph.runner.ViewRequestRunner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ViewRequestSteps {
    
    private WebDriver driver = ViewRequestRunner.driver;
    private LoginPage loginPage = ViewRequestRunner.loginPage;
    private HomePage homePage = ViewRequestRunner.homePage;
    
    // Temp
    private String value;
    
    // === EMPLOYEE ALL REQUESTS ====
    
    @Given("The employee is logged in and on the home page")
    public void the_employee_is_logged_in_and_on_the_home_page() {
        login("user1", "pass1");
    }

    @When("The user clicks the Your Requests button")
    public void the_user_clicks_the_your_requests_button() {
        homePage.btnRequests.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
    }

    @Then("The user will see all their requests in a table")
    public void the_user_will_see_all_their_requests_in_a_table() {
        assertEquals(true, homePage.tableItems.findElements(By.xpath("./tr")).size() == 3);
    }
    
    // === MANAGER ALL REQUESTS ===

    @Given("The manager is logged in and on the home page")
    public void the_manager_is_logged_in_and_on_the_home_page() {
        login("admin1", "secret1");
    }

    @When("The user clicks the Manage Requests button")
    public void the_user_clicks_the_manage_requests_button() {
        homePage.btnManage.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
    }

    @Then("The user will see all the requests")
    public void the_user_will_see_all_the_requests() {
        assertEquals(true, homePage.tableItems.findElements(By.xpath("./tr")).size() >= 9);
    }
    
    // === FILTER ===

    @Given("The user is logged in and on the home page")
    public void the_user_is_logged_in_and_on_the_home_page() {
        login("admin1", "secret1");
    }

    @When("The user clicks on the filter dropdown")
    public void the_user_clicks_on_the_filter_dropdown() {
        homePage.filter.click();
    }

    @When("Clicks a status filter option {string}")
    public void clicks_a_status_filter_option(String filter) {
        List<WebElement> options = homePage.filter.findElements(By.xpath("./option"));
        WebElement selected = null;
        for (WebElement option: options) {
            value = option.getAttribute("value");
            if (value.equals(filter)) {
                selected = option;
                break;
            }
        }
        selected.click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBe(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//*[@id=\"tableItems\"]/tr"), 0));
    }

    @Then("The user will see only requests of that option")
    public void the_user_will_see_only_requests_of_that_option() {
        List<RequestStatus> filters = new ArrayList<RequestStatus>(Arrays.asList(RequestStatus.getFilters(value)));
        List<WebElement> requests = homePage.tableItems.findElements(By.xpath("./tr"));
        for (WebElement request: requests) {
            WebElement status = request.findElement(By.xpath("./td[4]"));
            assertEquals(true, filters.contains(RequestStatus.valueOf(status.getText())));
        }
    }
    
    // === SPECIFIC REQUEST ===

    @When("The user clicks on a request in the table")
    public void the_user_clicks_on_a_request_in_the_table() {
        WebElement x = homePage.tableItems.findElement(By.xpath("./tr[1]/td[1]"));
        System.out.println(x.getText());
        homePage.tableItems.findElement(By.xpath("./tr[1]/td[1]/a")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleIs("Request Page"));
    }

    @Then("The user will be on the request page")
    public void the_user_will_be_on_the_request_page() {
        assertEquals("Request Page", driver.getTitle());
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
