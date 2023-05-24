package org.ranking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);
    private static final Properties prop;

    static {
        InputStream is = JedisUtil.class.getClassLoader().getResourceAsStream("ranking.properties");
        prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            LOGGER.error("Error occur while reading jedis config. cause : {}", e.getMessage());
        }

    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static String getProperty(String key, String defaultVal) {
        return prop.getProperty(key, defaultVal);
    }
}
