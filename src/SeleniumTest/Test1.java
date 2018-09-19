package SeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.lang.reflect.Method;


public class Test1 {
    /**
     * threadcase flag key:
     * 0 is the page reload case but timeout was exceeded
     * 1 is the page reload case and was successful
     * 2 is the page static case
     */
        public static void main(String[] args) throws InterruptedException {
            int timeoutSeconds = 10;
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.get("http://www.google.com/xhtml");
            WebElementLocator locator = new WebElementLocator();
            WebElement questionbox;
            try{
                questionbox = locator.WebElementLocator(driver,null,null,null,null,null,"q");
                WebDriverWait wait = new WebDriverWait(driver,timeoutSeconds);
                wait.until(ExpectedConditions.elementToBeClickable(questionbox));
                questionbox.sendKeys("WORKING");
                Method sendKeys = WebElement.class.getMethod("sendKeys", CharSequence[].class);
                CharSequence[] thing = {"THREADING"};
                isPageReloaded reload = new isPageReloaded(driver,questionbox,sendKeys,timeoutSeconds,thing);
                pageReloadTest(reload);
                Method submit = WebElement.class.getMethod("submit");
                reload = new isPageReloaded(driver,questionbox,submit,timeoutSeconds,null);
                pageReloadTest(reload);
            }
            catch (Exception e){
                questionbox = (driver.findElement(By.name("q")));
                Thread.sleep(5000);
                questionbox.sendKeys("ERROR");
                System.out.println(e);
            }
        }

    public static void testGoogleSearch() {
        // Optional, if not specified, WebDriver will search your path for chromedriver.
        //make sure you specify the file extension as well, otherwise it doesn't think it exists.
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.google.com/xhtml");
        try {
            Thread.sleep(4000);  // Let the user actually see something!
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("Youtube");
            searchBox.submit();
            Thread.sleep(4000);  // Let the user actually see something!
            WebElement youtubelink = driver.findElement(By.xpath("//*[@id='rso']/div[1]/div/div/div/div/h3/a"));
            youtubelink.click();
            Thread.sleep(4000);  // Let the user actually see something!
            WebElement youtubesearch = driver.findElement(By.xpath("//*[@id=\"search\"]"));
            youtubesearch.sendKeys("ac dc thunderstruck official video");
            try{
                searchBox.submit();
            }
            catch (org.openqa.selenium.StaleElementReferenceException s){
                WebElement youtubeResearch = driver.findElement(By.xpath("//*[@id='search-icon-legacy']"));
                youtubeResearch.click();
            }
            Thread.sleep(4000);  // Let the user actually see something!
            WebElement youtubevidlink = driver.findElement(By.linkText("AC/DC - Thunderstruck (Official Video)"));
            youtubevidlink.click();
            Thread.sleep(6000);
            try{
                WebElement PREskipadbutton = driver.findElement(By.className("videoAdUiPreSkipButton"));
                WebElement skipadbutton = null;
                while(PREskipadbutton.isDisplayed()){
                    WebElement temp = null;
                    try{
                        temp = driver.findElement(By.className("videoAdUiSkipButton videoAdUiAction videoAdUiFixedPaddingSkipButton"));
                        System.out.println("Polled for skip ad button with Classname, worked");
                    }
                    catch (org.openqa.selenium.NoSuchElementException e){
                        try{
                            System.out.println("Polled for skip ad button with Classname, didn't work");
                            temp = driver.findElement(By.xpath("//*[@id=\"movie_player\"]/div[12]/div/div/div[5]/button"));
                            System.out.println("Polled for skip ad button with Xpath, worked");
                        }
                        catch (org.openqa.selenium.NoSuchElementException e1){
                            System.out.println("Polled for skip add button both Xpath and Classname, didn't work");
                        }
                    }
                    if (skipadbutton == null){
                        skipadbutton = temp;
                        System.out.println("found skip ad button and assigned it");
                        break;
                    }
                }
                while (!(skipadbutton.isDisplayed() && skipadbutton.isEnabled())){
                    Thread.sleep(500);
                    System.out.println("found skipad button, waiting for it to be displayed and enabled");
                }
                System.out.println("skipad button is enabled.");
                skipadbutton.click();
                System.out.println("Skip ad button clicked, should move to video.");
                Thread.sleep(60000);
            }
            catch (org.openqa.selenium.NoSuchElementException e){
                System.out.println("No ad! Yay! Enjoy video");
                Thread.sleep(10000);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
    public static void pageReloadTest(isPageReloaded reload) throws InterruptedException {
        Thread reloadthread = new Thread(reload);
        reloadthread.start();
        //relate result var here to thread result to effectively track how the thread is executing
        int result = 0;
        //wait until one of these things happens before main moves on to another webelement
        //TODO fix the race condition that keeps consuming the webelement here, for page reloads that change the DOM this causes
        //The other thread to throw a bad exception and it makes it look like the page wasn't reloaded when really it was. since
        // both threads execute the same method but essentially twice, the implementation needs to change. if the static searching thread
        //executes a method that reloads the page it consumes the webelement so the reload searching thread can't see it,
        // interrupt the other thread and say 'HEY this thing causes a page reload!'.
        //TODO maybe consider making it one thread that has the wait condition of expected value, checks for timeout exception
        //and it breaks when the expected value condition is met out of the run function. Might make it easier. Main can be
        //the other thread that constantly checks its condition and tells the thread to stop right there, no need for break?
        //also this would eliminate the double consumption of webelements.
        while(result == 0){
            if (reload.getThreadcase() == 1){
                //if page reloaded, stop waiting for static page, finish reload thread and continue with main
                reloadthread.join();
                System.out.println("page reloaded");
                result = 1;
            }
            else if(reload.getThreadcase() == 2){
                //if page remained static and timeout exceeded, finish static thread and continue with main
                reloadthread.interrupt();
                System.out.println("page was static");
                result = 2;
            }
        }
    }
}

