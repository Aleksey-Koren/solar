package io.solar.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {
    private static final Properties properties = new Properties();

    static {
        try(InputStream resource = PropertyUtils.class.getClassLoader().getResourceAsStream("database.properties")) {
            properties.load(resource);
        } catch (IOException e) {
            throw new RuntimeException("Cannot find database.properties");
        }
    }

    public static String getProperty(String propertyName) {

        return properties.getProperty(propertyName);
    }
}
