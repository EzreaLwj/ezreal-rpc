package com.ezreal.rpc.core.spi;

import com.ezreal.rpc.core.filter.IClientFilter;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义SPI加载器
 *
 * @author Ezreal
 * @Date 2023/10/22
 */
public class ExtensionLoader {

    private static final String prefix = "META-INF/ezrealrpc/";

    public static final Map<String, LinkedHashMap<String, Class<?>>> CLASS_CACHE = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ExtensionLoader extensionLoader = new ExtensionLoader();
        extensionLoader.load(IClientFilter.class);
        String name = IClientFilter.class.getName();
        LinkedHashMap<String, Class<?>> linkedHashMap = CLASS_CACHE.get(name);
        linkedHashMap.forEach((k, v) -> {
            System.out.println("key: " + k + " value: " + v);
        });
    }

    public void load(Class<?> clazz) throws IOException, ClassNotFoundException {
        if (clazz == null) {
            throw new RuntimeException("the class is null...");
        }

        // 获取文件路径
        String filePath = prefix + clazz.getName();
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources(filePath);

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            InputStreamReader reader = new InputStreamReader(url.openStream());

            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;

            // 读取文件的每一行
            LinkedHashMap<String, Class<?>> linkedHashMap = new LinkedHashMap<>();
            while ((line = bufferedReader.readLine()) != null) {

                if (line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split("=");
                String implClassName = split[0];
                String className = split[1];
                linkedHashMap.put(implClassName, Class.forName(className));
            }

            if (CLASS_CACHE.containsKey(clazz.getName())) {
                CLASS_CACHE.get(clazz.getName()).putAll(linkedHashMap);
            } else {
                CLASS_CACHE.put(clazz.getName(), linkedHashMap);
            }
        }
    }

}
