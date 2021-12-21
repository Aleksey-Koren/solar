package io.solar;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.solar.utils.PropertyUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws SQLException {

        String url = PropertyUtils.getProperty("db.url");
        String username = PropertyUtils.getProperty("db.username");
        String password = PropertyUtils.getProperty("db.password");

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        boolean stop = false;
        Map<String, Map<String, String>> results = initResults();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT title, type FROM planets");
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                if(results.get(title) != null) {
                    continue;
                }
                Map<String, String> rawData = fetchWikiPage(
                        driver,
                        title,
                        resultSet.getString("type"),
                        0
                );
                if(rawData.size() > 0) {
                    results.put(title, rawData);
                } else {
                    System.out.println("fail to load data for " + title);
                }
                try {
                    FileWriter fileWriter = new FileWriter("C:\\Users\\pc\\solar\\planets\\data.json");
                    new ObjectMapper().writeValue(fileWriter, results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(stop) {
                    return;
                }
            }
        } finally {
            try {
                driver.quit();
            } finally {

            }
        }

    }

    private static Map<String, Map<String, String>> initResults() {
        try {
            FileReader fileWriter = new FileReader("C:\\Users\\pc\\solar\\planets\\data.json");
            return new ObjectMapper().readValue(fileWriter, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private static Map<String, String> fetchWikiPage(WebDriver webDriver, String title, String type, int iteration) {

        String searchQuery = prepareQuery(title, type);
        webDriver.get(searchQuery);
        var googleResults = webDriver.findElements(By.cssSelector("#search .g"));
        boolean linkExists = false;
        for(var el : googleResults) {
            String href = el.findElement(By.tagName("a")).getAttribute("href");
            if(href.indexOf("https://en.wikipedia.org/") == 0 && removeSpecialChars(href).contains(removeSpecialChars(title))) {
                linkExists = true;
                webDriver.get(href);
                break;
            }
        }
        if(!linkExists) {
            if(iteration < 2) {
                try {
                    webDriver.findElement(By.className("rc-anchor-container"));
                    iteration++;
                    return fetchWikiPage(webDriver, title, type, iteration);
                } catch (NoSuchElementException e) {
                    //ignore
                }
            }
            System.out.println("fail to find planetoid " + title + " in google results");
            return new HashMap<>();
        }


//        webDriver.findElement(By.xpath("//*[@id=\"rso\"]/div[1]/div/div/div/div[1]/a")).click();
//        WebElement infobox = webDriver.findElement(By.className("infobox"));

        var tr = webDriver.findElements(By.cssSelector(".infobox > tbody >tr"));
        Map<String, String> map = new HashMap<>();
        for (WebElement we : tr) {
            List<WebElement> we1 = we.findElements(By.cssSelector("th, td")).stream()
                    .filter(s -> {
                        WebElement parent = (WebElement) ((JavascriptExecutor) webDriver).executeScript(
                                "return arguments[0].parentNode;", s);
                        return parent.equals(we);
                    }).collect(Collectors.toList());
            if (we1.size() != 2) {
                String content = we1.stream().map(WebElement::getText).collect(Collectors.joining("|"));
                if(unknownInfobox(content)) {
                    System.out.println("infobox wrong format " + content);
                }
                continue;
            }
            map.put(we1.get(0).getText(), we1.get(1).getText());
        }
        var i = 0;
        for(WebElement image : webDriver.findElements(By.cssSelector(".infobox img"))) {
            var srcset = image.getAttribute("srcset");
            if(srcset != null && !"".equals(srcset)) {
                map.put("srcset_" + i, srcset);
            }

            var src = image.getAttribute("src");
            if(srcset != null && !"".equals(srcset)) {
                map.put("src_" + i, srcset);
            }

            i++;
        }
        return map;

    }

    private static String removeSpecialChars(String title) {
        return title.replaceAll("[\\\\ _/\t+]", "");
    }

    private static boolean unknownInfobox(String content) {
        switch (content.trim()) {
            case "Discovery":
            case "Observation data":
            case "Orbital characteristics":
            case "Physical characteristics":
            case "Rotation characteristics":
            case "Photospheric composition (by mass)":
            case "Mercury in true color (by MESSENGER in 2008)":
            case "Designations":
            case "Orbital characteristics[5]":
            case "Epoch J2000":
            case "Surface temp. min mean max\n"+
            "0°N, 0°W [13] -173 °C 67 °C 427 °C\n"+
            "85°N, 0°W[13] -193 °C -73 °C 106.85 °C":
            case "Atmosphere":
            case "":
                return false;
            default:
                String[] startsWith = new String[]{
                    "The Blue Marble, the most widely used photograph",
                    "True-color image taken in",
                    "False-color image taken in",
                    "Physical characteristics[",
                        "Orbital characteristics [",
                    "Orbital characteristics [",
                    "Rediscovery images of ",
                        "Pictured in natural color",
                        "Atmosphere[",
                        "Discovery [",
                        "Discovery image",
                        "Discovery[",
                        "Discovery [",
                        "Epoch 1",
                        "Epoch 2",
                        "Epoch 3",
                        "Epoch December",
                    "Epoch J",
                        "Orbital characteristics",

                };
                for(String st : startsWith) {
                    if(content.toLowerCase().startsWith(st.toLowerCase())) {
                        return false;
                    }
                }
                return true;
        }
    }

    private static String prepareQuery(String title, String type) {
        return "https://google.com/search?q=" + title + ("moon".equals(type) ? "+(moon)" : "+solar+system") + "+site:en.wikipedia.org";
    }

    private static void forceClick(WebDriver wd, WebElement we) {
        Actions builder = new Actions(wd);
        builder.moveToElement(we);
        builder.click();
        builder.build().perform();
    }
}
