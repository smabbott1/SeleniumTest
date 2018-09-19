package SeleniumTest;

import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class WebElementLocator {
    private String id;
    private String xpath;
    private String className;
    private String linkText;
    private String tagName;
    private String name;
    //TODO consider editing toString to take in ParamTuple param name and the result so that you can print what Webelements were received
    public WebElement WebElementLocator(WebDriver driver, String id, String xpath, String className, String linkText, String tagName, String name){
        //NOTE: precedence of locators is based on the order of the parameters. ie id has higher precedence because it's the first parameter.
        //NOTE 2: locators are referenced by name, thus the parameters must have the same name as the method name of the locator method in By.Class
        this.id = id;
        this.xpath = xpath;
        this.className = className;
        this.linkText = linkText;
        this.tagName = tagName;
        this.name = name;
        int timeoutSeconds = 10;
        String[] args = {id,xpath,className,linkText,tagName,name};
        String[] argNames = {"id","xpath","className","linkText","tagName","name"};
        HashMap<String, Method> locateMethod = new HashMap<>();
        HashSet<List<String>> parameterSet = new HashSet<>();
        //if method in By.Class is a locator then store it by it's name
        for (Method method:By.class.getMethods()) {
            if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String.class)){
                locateMethod.put(method.getName(),method);
            }
        }
        for (int i = 0; i < args.length; i++) {
            parameterSet.add(Arrays.asList(argNames[i],args[i]));
        }
        //for each parameter, find the locator method based on the common method name and parameter name and invoke it on the parameter's value
        //it does this by finding the Tuple parameter pair in a set (preserves order) and mapping via the parameter name in the parameter Tuple.
        //(quick access time).
        //initialize the result list
        ArrayList<Object> results = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while(results.size() == 0 && stopWatch.getTime(TimeUnit.SECONDS) < timeoutSeconds){
            //try each method of finding the webelement and if it is found, add it to the result list (hence breaking out of the while loop)
            for (List<String> paramTuple:parameterSet) {
                try{
                    Method temp = locateMethod.get(paramTuple.get(0));
                    WebElement addthis = driver.findElement((By) temp.invoke(null,paramTuple.get(1)));
                    results.add(addthis);
                }
                catch (NoSuchElementException | InvocationTargetException e){
                    //do not add exception to list or print it, this one should be ignored, its thrown on purpose
                }
                catch (IllegalAccessException e){
                    //these should be printed as they reflect real errors.
                    e.printStackTrace();
                }
            }
        }
        stopWatch.stop();
        if (results.size() == 0){
            throw new org.openqa.selenium.NoSuchElementException("no webElement could be found for " + this.toString(results));
        }
        else{
            return (WebElement) results.get(0);
        }
    }
    //Call this toString if no webElement could be found so error can be diagnosed.
    public String toString(ArrayList<Object> results){
        return "Id: tried " + this.id + " got " + results.get(0) + '\n' +
               "xpath: tried " + this.xpath + " got " + results.get(1) + '\n' +
               "className: tried " + this.className + " got " + results.get(2) + '\n' +
               "linkText: tried " + this.linkText + " got " + results.get(3) + '\n' +
               "tagName: tried " + this.tagName + " got " + results.get(4) + '\n' +
               "name: tried " + this.name + " got " + results.get(5);
    }
}
