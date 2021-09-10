package com.nautilus.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Queries {

    private static final Map<String, Properties> propsMap = new HashMap<>();

    public static Properties getQueries(String propFileName) {
        InputStream is =
                Queries.class.getResourceAsStream("/" + propFileName);
        RuntimeException e = new RuntimeException("Unable to load property file: " + propFileName);
        if (is == null) {
            log.error(e.getMessage());
            throw e;
        }
        //cache
        if (propsMap.get(propFileName) == null) {
            Properties props = new Properties();
            try {
                props.load(is);
            } catch (IOException ex) {
                log.error(e.getMessage(), "\n" + ex.getMessage());
                throw e;
            }
            propsMap.put(propFileName, reconstructInherited(props));
        }
        return propsMap.get(propFileName);
    }

    private static Properties reconstructInherited(Properties properties) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]*)}");
        properties.forEach((k, v) -> {
            Matcher matcher = pattern.matcher((String) v);
            while (matcher.find()) {
                properties.put(k, matcher.replaceFirst(
                        Matcher.quoteReplacement(properties.getProperty(matcher.group().substring(2, matcher.group().length() - 1)))));
                matcher = pattern.matcher((String) properties.get(k));
            }
        });
        return properties;
    }

    public static String getQuery(String propFileName, String query) {
        return getQueries(propFileName).getProperty(query);
    }

}