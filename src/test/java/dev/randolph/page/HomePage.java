package dev.randolph.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
    
    private WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }
    
    // Buttons
    
    @FindBy(id = "btnRequests")
    public WebElement btnRequests;
    
    @FindBy(id = "btnManage")
    public WebElement btnManage;
    
    @FindBy(xpath = "/html/body/div/nav/ul/li[3]/button")
    public WebElement btnLogout;
    
    @FindBy(id = "btnNewRequest")
    public WebElement btnNewRequest;
    
    // Table
    
    @FindBy(id = "filter")
    public WebElement filter;
    
    @FindBy(id = "tableItems")
    public WebElement tableItems;
}
