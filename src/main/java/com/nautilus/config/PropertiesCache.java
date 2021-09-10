package com.nautilus.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("unused")
public class PropertiesCache {
    private final Properties configProp = new Properties();

    private PropertiesCache() {
        //Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //singleton pattern
    private static class LazyHolder {
        private static final PropertiesCache INSTANCE = new PropertiesCache();
    }

    public static PropertiesCache getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getProperty(String key) {
        return configProp.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

}