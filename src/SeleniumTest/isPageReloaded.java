package SeleniumTest;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class isPageReloaded implements Runnable {
    WebElement webElement;
    Method methodToUse;
    WebDriver driver;
    int timeoutSeconds;
    Object args;
    private volatile int threadcase = 0;

    /**
     * threadcase flag key:
     * 0 is the initial dummy value
     * 1 is the page reload case
     * 2 is the page static case
     */
    @Override
    public void run(){
        try {
            System.out.println(methodToUse.getName() + webElement + args);
            Object[] array;
            if (args == null){
                array = new Object[]{};
            }
            else{
                array = new Object[]{args};
            }
            methodToUse.invoke(webElement,array);
            //need to make sure some sort of flag is set when timeout is exceeded vs normal wait procedure.
            WebDriverWait wait = new WebDriverWait(driver,timeoutSeconds);
            try{
                wait.until(ExpectedConditions.stalenessOf(webElement));
                String script = "function test(arg,callback) {callback(arg);}" +
                        "function Return(arg){console.log(arg);}" +
                        "var a = 2;" +
                        "window.addEventListener('onload', test(a,Return));";
                JavascriptExecutor js = (JavascriptExecutor)driver;
                js.executeScript(script);
                
                threadcase = 1;
            }
            catch (TimeoutException e){
                threadcase = 2;
            }
        } catch (IllegalAccessException | InvocationTargetException e){
                 e.printStackTrace();
        }
    }
    public isPageReloaded(WebDriver driver, WebElement webElement, Method methodToUse, int timeoutSeconds, Object args){
        this.webElement = webElement;
        this.methodToUse = methodToUse;
        this.driver = driver;
        this.timeoutSeconds = timeoutSeconds;
        this.args = args;
    }

    public int getThreadcase() {
        return threadcase;
    }
}
