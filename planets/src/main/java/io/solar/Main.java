package io.solar;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.solar.utils.PropertyUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {

        String url = PropertyUtils.getProperty("db.url");
        String username = PropertyUtils.getProperty("db.username");
        String password = PropertyUtils.getProperty("db.password");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String pass = resultSet.getString("password");
                System.out.println(login + " " + pass);
            }
        }

        WebDriverManager.chromedriver().setup();
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://google.com/");
        webDriver.findElement(By.name("q")).sendKeys("moon site:en.wikipedia.org");
        webDriver.findElement(By.name("q")).submit();
        var element = webDriver.findElements(By.cssSelector("#search .g")).get(1);
        forceClick(webDriver, element.findElement(By.tagName("a")));

//        webDriver.findElement(By.xpath("//*[@id=\"rso\"]/div[1]/div/div/div/div[1]/a")).click();
//        WebElement infobox = webDriver.findElement(By.className("infobox"));

        var tr = webDriver.findElements(By.cssSelector(".infobox > tbody >tr"));
        Map<String, String> map = new HashMap<>();
        for(WebElement we : tr){
            List<WebElement> we1 = we.findElements(By.cssSelector("th, td")).stream()
                    .filter(s -> {WebElement parent = (WebElement) ((JavascriptExecutor) webDriver).executeScript(
                            "return arguments[0].parentNode;", s);
                    return parent.equals(we);}).collect(Collectors.toList());
            if (we1.size() != 2) {
                System.out.println("!!! Error > 2 " + we1.stream().map( s -> s.getText()).collect(Collectors.joining("|")));
                continue;
            }
            map. put(we1.get(0).getText(), we1.get(1).getText());
        }
        System.out.println(map);

        webDriver.close();
        webDriver.quit();
    }

    private static void forceClick(WebDriver wd, WebElement we) throws InterruptedException {
        ((JavascriptExecutor) wd).executeScript("arguments[0].scrollIntoView(true);", we);
        Thread.sleep(500); //not sure why the sleep was needed, but it was needed or it wouldnt work :(
        we.click();
    }
}
