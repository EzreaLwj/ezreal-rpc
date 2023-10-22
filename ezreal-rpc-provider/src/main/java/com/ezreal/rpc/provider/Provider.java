package com.ezreal.rpc.provider;

import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.server.ApplicationShutdownHook;
import com.ezreal.rpc.core.server.Server;
import com.ezreal.rpc.core.server.ServiceWrapper;
import com.ezreal.rpc.test.UserServiceImpl;

/**
 * @author Ezreal
 * @Date 2023/10/22
 */
public class Provider {

    public static void main(String[] args) throws Exception {

        // 初始化配置
        Server server = new Server();
        server.initServerConfig();

        // 配置事件监听
        ListenerLoader listenerLoader = new ListenerLoader();
        listenerLoader.init();

        ServiceWrapper serviceWrapper = new ServiceWrapper(new UserServiceImpl(), "dev");
        serviceWrapper.setServiceToken("token-a");
        serviceWrapper.setLimit(2);
        // 暴露服务
        server.exportService(serviceWrapper);
        // 注册钩子
        ApplicationShutdownHook.registryShutDownHook();

        server.setOnApplication();
    }
}
