package com.ezreal.rpc.consumer;

import com.ezreal.rpc.core.client.Client;
import com.ezreal.rpc.core.client.ConnectHandler;
import com.ezreal.rpc.core.client.RpcReference;
import com.ezreal.rpc.core.client.RpcReferenceWrapper;
import com.ezreal.rpc.test.UserService;

/**
 * @author Ezreal
 * @Date 2023/10/22
 */
public class Consumer {

    public static void main(String[] args) throws Exception {

        // 创建客户端获取代理工厂
        Client client = new Client();
        client.initClientConfig();
        RpcReference rpcReference = client.initClientApplication();

        RpcReferenceWrapper<UserService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setGroup("dev");
        rpcReferenceWrapper.setServiceToken("token-a");
        rpcReferenceWrapper.setAimClass(UserService.class);
        // 获取代理对象，代理对象拦截方法，通过SEND_QUEUE通信
        UserService userService = rpcReference.get(rpcReferenceWrapper);

        // 订阅服务，将信息加入到 SUBSCRIBE_SERVICE_LIST 集合中
        client.subscribeService(UserService.class);
        ConnectHandler.setBootstrap(client.getBootstrap());

        // 拉取订阅的列表，建立netty通信，将信息存入到CONNECT_MAP中
        client.doConnectServer();

        // 开启异步线程，获取SEND_QUEUE中的信息
        client.startClientApplication();

        for (int i = 0; i < 22; i++) {
            Thread.sleep(500);
            // 发送请求消息
            System.out.println(userService.getUserInfo("lwj", i));
        }
    }

}
