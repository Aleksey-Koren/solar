package io.solar;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.solar.utils.PropertyUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws SQLException {

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
    }
}
