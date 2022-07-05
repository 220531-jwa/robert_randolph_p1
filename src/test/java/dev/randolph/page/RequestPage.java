package dev.randolph.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RequestPage {
    
    private WebDriver driver;
    
    public RequestPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
    }
    
    // Buttons
    
    @FindBy(xpath = "/html/body/div/div[2]/div[1]/button")
    public WebElement btnBack;
    
    @FindBy(id = "btnSubmit")
    public WebElement btnSubmit;
    
    // Input Fields
    
    @FindBy(id = "inputEventType")
    public WebElement inputEventType;
    
    @FindBy(id = "inputCost")
    public WebElement inputCost;
    
    @FindBy(id = "inputGradeFormat")
    public WebElement inputGradeFormat;
    
    @FindBy(id = "inputCutoff")
    public WebElement inputCutoff;
    
    @FindBy(id = "inputDescription")
    public WebElement inputDescription;
    
    @FindBy(id = "inputLocation")
    public WebElement inputLocation;
    
    @FindBy(id = "inputStartDate")
    public WebElement inputStartDate;
    
    @FindBy(id = "inputStartTime")
    public WebElement inputStartTime;
    
    @FindBy(id = "inputJustification")
    public WebElement inputJustification;
    
    // Update Fields
    
    @FindBy(id = "inputGrade")
    public WebElement inputGrade;
    
    @FindBy(id = "inputReimAmount")
    public WebElement inputReimAmount;
    
    @FindBy(id = "inputStatus")
    public WebElement inputStatus;
    
    @FindBy(id = "inputReason")
    public WebElement inputReason;
    
    // Misc
    
    @FindBy(id = "firstName")
    public WebElement firstName;
}
