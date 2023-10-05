package com.ezreal.rpc.test;

import com.ezreal.rpc.core.common.config.PropertiesLoader;
import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.event.UpdateServiceEvent;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.ZookeeperRegister;
import org.junit.Test;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class TestApi {

    @Test
    public void testConfig() {
        PropertiesLoader.loadConfiguration();

        Integer port = PropertiesLoader.getPropertiesInteger("ezrealRpc.port");
        System.out.println(port);
    }

    @Test
    public void testConnectToZK() {
        ZookeeperRegister zookeeperRegister = new ZookeeperRegister("43.136.49.11:2181");
        URL url = new URL();
        url.setServiceName("com.ezreal.rpc.core.common.test");
        HashMap<String, String> params = new HashMap<>();
        params.put("port", "1234");
        params.put("host", "localhost");
        url.setParams(params);
        url.setApplicationName("ezreal");
        zookeeperRegister.register(url);

        System.out.println(zookeeperRegister.getProviderIps(url.getServiceName()));
    }

    public static void main(String[] args) {
        ListenerLoader.init();
        UpdateServiceEvent updateServiceEvent = new UpdateServiceEvent();
        updateServiceEvent.setData("hello world");
        ListenerLoader.send(updateServiceEvent);
    }
}
