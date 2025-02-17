package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.BasePage;
import pages.Page;
import utils.ConfigProperties;
import utils.Constant;
import utils.DataSetAccess;
import utils.Helper;
import utils.DataSetAccess;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.NoSuchElementException;

public class BaseTest {
    private String url=null;
    public static WebDriver driver;
    public static String resourcesRoot = "./resources/";// Set Path f resources
    FluentWait wait;
    public static Page page;
    public static ConfigProperties configProperties;
    public static Constant constant;
    public static String baseUrl;
    public static  int dataRowCount = 0;
    public static DataSetAccess dataSetAccess;
    public static Helper helper;
    public static SoftAssert softAssert;



    public BaseTest(String url){this.url = url;}


    public void init() throws IOException {

        dataSetAccess = new DataSetAccess();
        softAssert = new SoftAssert();
        configProperties = new ConfigProperties();
        constant = new Constant();
        helper = new Helper();
        url = configProperties.config.getProperty("BASE_URL");
    }

    @BeforeMethod
    public void beforeMethod(Method method)
    {
//        Allure.addAttachment(method.getName(), screenshot());
    }

    @AfterMethod
    public void afterMethod(Method method)
    {
//        tearDown();
//        Allure.addAttachment(method.getName(),screenshot());
    }


    @BeforeSuite
    @Parameters(value = {"browser"})
    public void setupTest(String browser) throws IOException {

        init();

        if (browser.equals("chrome")){
            System.setProperty("webdriver.chrome.driver",resourcesRoot+"drivers\\chromedriver.exe");

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");

           driver = new ChromeDriver(options);

            System.out.println("Chrome Browser is Selected");
        }
        else if(browser.equals("firefox")){
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
            System.out.println("Mozila Firefox Browser is Selected");
        }
        else{
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
            System.out.println("Edge Browser is Selected");
        }


        wait = new FluentWait(driver);
        page = new BasePage(driver);
        driver.manage().window().maximize();
        driver.get(this.url);


        wait.withTimeout(Duration.ofSeconds(Long.valueOf(configProperties.config.getProperty("TIMEOUT_IN_SECONDS"))));
        wait.withTimeout(Duration.ofSeconds(Long.valueOf(configProperties.config.getProperty("POLLING_IN_MILISECONDS"))));
    }




    public static void captureScreenshot(WebDriver driver, String testName) {
        // Get the current date and time
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(now);

        // Define the screenshot directory
        String screenshotDir = "resources/Screenshot";
        File screenshotDirFile = new File(screenshotDir);
        if (!screenshotDirFile.exists()) {
            screenshotDirFile.mkdir();
        }

        // Define the screenshot file path
        String screenshotPath = screenshotDir + File.separator + testName + "_" + timestamp + ".png";

        // Capture screenshot and save it
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshotFile, new File(screenshotPath));
            System.out.println("Screenshot captured: " + screenshotPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Capture screenshot
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        // Attach screenshot to Allure
        Allure.addAttachment(testName + " - Screenshot", new ByteArrayInputStream(screenshotBytes));
    }


    @AfterSuite
    public void tearDown(){
        driver.quit();

    }

}
