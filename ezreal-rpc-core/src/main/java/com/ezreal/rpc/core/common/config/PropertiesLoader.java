package com.ezreal.rpc.core.common.config;

import com.ezreal.rpc.core.common.utils.CommonUtil;

import java.io.FileInputStream;
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

    private static final String FILE_PATH = "D:\\IDEA_PROJECT3\\ezreal-rpc\\ezreal-rpc-core\\src\\main\\resources\\ezreal-rpc.properties";

    public static void loadConfiguration() {
        if (properties != null) {
            return;
        }
        try(FileInputStream fileInputStream = new FileInputStream(FILE_PATH)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
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
