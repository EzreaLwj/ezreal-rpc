package com.ezreal.rpc.core.register;

import com.ezreal.rpc.core.register.zookeeper.ProviderNodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 自定义 zk 服务节点的路径
 *
 * @author Ezreal
 * @Date 2023/10/4
 */
public class URL {

    /**
     * 应用名称 ezreal-rpc
     */
    private String applicationName;

    /**
     * 服务名称 com.ezreal.core.test.UserService
     */
    private String serviceName;

    /**
     * 其他参数：IP、Port等
     */
    private HashMap<String, String> params = new HashMap<>();

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 将URL转换为写入zk的provider节点下的一段字符串 即provider节点的值
     * 应用名;服务名;主机名:端口;时间戳
     *
     * @return 服务提供者的节点
     */
    public static String buildProviderUrlStr(URL url) {
        String host = url.getParams().get("host");
        String port = url.getParams().get("port");

        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ":" + port + ";" + System.currentTimeMillis() + ";100").getBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 将URL转换为写入zk的consumer节点下的一段字符串 即consumer节点的值
     * 应用名;服务名;主机名;时间戳
     *
     * @param url url
     * @return 结果
     */
    public static String buildConsumerUrlStr(URL url) {
        String host = url.getParams().get("host");
        return new String((url.getApplicationName() + ";" + url.getServiceName() + ";" + host + ";" + System.currentTimeMillis()).getBytes(), StandardCharsets.UTF_8);

    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    /**
     * 将 url 转化为实体类
     *
     * @param providerUrlStr url
     * @return 实体类
     */
    public static ProviderNodeInfo buildURLFromUrlStr(String providerUrlStr) {
        String[] strings = providerUrlStr.split("/");

        ProviderNodeInfo providerNodeInfo = new ProviderNodeInfo();
        providerNodeInfo.setServiceName(strings[1]);
        providerNodeInfo.setAddress(strings[2]);
        providerNodeInfo.setRegistryTime(strings[3]);
        providerNodeInfo.setWeight(Integer.valueOf(strings[4]));
        return providerNodeInfo;
    }
}
