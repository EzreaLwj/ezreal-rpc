package com.ezreal.rpc.core.common.config;

import com.ezreal.rpc.core.common.utils.CommonUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * 读取配置文件
 * @author Ezreal
 * @Date 2023/10/5
 */
public class PropertiesLoader {

    private static Properties properties;

    private static HashMap<String, String> propertiesMap = new HashMap<>();

    private static final String FILE_PATH = "ezreal-rpc.properties";

    public static void loadConfiguration() {
        if (properties != null) {
            return;
        }

        try {
            properties = new Properties();
            InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream(FILE_PATH);
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static String getPropertiesStr(String key) {

        if (properties == null) {
            return null;
        }
        if (CommonUtil.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return String.valueOf(propertiesMap.get(key));
    }

    public static Integer getPropertiesInteger(String key) {

        if (properties == null) {
            return null;
        }
        if (CommonUtil.isEmpty(key)) {
            return null;
        }
        if (!propertiesMap.containsKey(key)) {
            String value = properties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return Integer.valueOf(propertiesMap.get(key));
    }
}
