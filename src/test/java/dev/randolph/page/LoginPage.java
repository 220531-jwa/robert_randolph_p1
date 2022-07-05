package dev.randolph.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    
    private WebDriver driver;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }
    
    @FindBy(id = "inputUsername")
    public WebElement inputUsername;
    
    @FindBy(id = "inputPassword")
    public WebElement inputPassword;
    
    @FindBy(xpath = "/html/body/div/div/div/form/div[4]/button")
    public WebElement signInBtn;
}
