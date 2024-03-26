import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;


//Zadatak 1
//1. Navigate to https://www.booking.com/
//2. Type in "Kopaonik" into search field.
//3. Choose the duration of the stay.
//4. Choose number of guests and rooms.
//5. Click "Search".
//6. Verify that the search results are displayed.

public class ZavrsniTestNG {

    public static WebDriver driver;
    String URL = "https://www.booking.com/";

    @BeforeMethod
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver_121.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.get(URL);

    }
    @Test
    @Parameters({"place","check_in","check_out","adults","children","rooms","child_years_old"})
    public void Booking(String place, String check_in, String check_out, int adults, int children, int rooms, String child_years_old) throws InterruptedException {


        //scroll
        scrollIntoView(driver, driver.findElement(By.cssSelector("[data-testid='date-display-field-start']")));
        //polje za place
        driver.findElement(By.cssSelector("[id=\':re:\']")).sendKeys(place);
        driver.findElement(By.cssSelector("[data-testid='date-display-field-start']")).click();

        Duration timeout = Duration.ofSeconds(10);
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".f73e6603bf")));

        WebElement nextMonthBtn = driver.findElement(By.xpath("//button[@aria-label='Next month']"));

        // danasnji datum
        LocalDate currentDate = LocalDate.now();

        LocalDate selectedDate = LocalDate.parse(check_in);

        // racunaje vremena od danasnjeg do selktovanog
        Period period = Period.between(currentDate, selectedDate);

        // racunanje mjeseca izmedju dva datuma
        int monthsDifference = period.getYears() * 12 + period.getMonths();
        //for petlja za pronalazenje mjeseca
        for(int i = 0; i<monthsDifference; i++) {
            nextMonthBtn.click();
        }

        // pronalazenje zeljenog datuma
        driver.findElement(By.xpath(String.format("//*[@data-date='%s']", check_in))).click();

        scrollIntoView(driver, driver.findElement(By.cssSelector("[data-testid='date-display-field-end']")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".f73e6603bf")));

        // pronalazenje zeljenog datuma
        driver.findElement(By.xpath(String.format("//*[@data-date='%s']", check_out ))).click();

        // polje adults, children rooms
        driver.findElement(By.xpath("//*[@id=\'indexsearch\']/div[2]/div/form/div[1]/div[3]/div/button")).click();
        for (int i=0; i<adults; i++){
            // polje + adults
            driver.findElement(By.xpath("//*[@id=\":rf:\"]/div/div[1]/div[2]/button[2]")).click();
        }

        // polje + rooms
        for (int i = 0; i < rooms; i++) {
            driver.findElement(By.xpath("//*[@id=\":rf:\"]/div/div[3]/div[2]/button[2]")).click();

        }

        // polje + children
        for (int i=0; i<children; i++){
            driver.findElement(By.xpath("//*[@id=\":rf:\"]/div/div[2]/div[2]/button[2]")).click();
        }
//dodavanje godina djece
        List<String> child_years_old_list = Arrays.asList(child_years_old.split(","));
        for(int i = 0; i< children; i++) {
            List<WebElement> refreshedDropdownMenus = driver.findElements(By.xpath("//div[@data-testid='kids-ages-select']"));
            if(!refreshedDropdownMenus.isEmpty()) {
                WebElement dropdownMenu = refreshedDropdownMenus.get(i);

                System.out.println(dropdownMenu);
                WebElement selectElement = dropdownMenu.findElement(By.xpath(".//select"));
                Select dropdown = new Select(selectElement);

                dropdown.selectByVisibleText(child_years_old_list.get(i));
            }
        }

       // polje search
       driver.findElement(By.xpath("//*[@id=\"indexsearch\"]/div[2]/div/form/div[1]/div[4]/button/span")).click();
Thread.sleep(1000);

       // Assert da li su rezultati prikazani
        WebElement propertyContainer = driver.findElement(By.xpath("//div[@data-testid='property-card']"));
        Assert.assertTrue(propertyContainer.isDisplayed());

    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // Scroll element
    public static void scrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
