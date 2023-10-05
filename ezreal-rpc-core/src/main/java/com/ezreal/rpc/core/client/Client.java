package com.ezreal.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.*;
import com.ezreal.rpc.core.common.config.ClientConfig;
import com.ezreal.rpc.core.common.config.PropertiesBootStrap;
import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.proxy.jdk.JDKProxyFactory;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.AbstractRegister;
import com.ezreal.rpc.core.register.zookeeper.ZookeeperRegister;
import com.ezreal.rpc.test.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.REQUEST_QUEUE;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.SUBSCRIBE_SERVICE_LIST;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class Client {

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    private ClientConfig clientConfig;

    private NioEventLoopGroup eventExecutors;

    private Bootstrap bootstrap;

    private AbstractRegister register;

    private ListenerLoader listenerLoader;

    public Client() {
    }

    public Client(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference initClientApplication() throws InterruptedException {
        eventExecutors = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new ClientHandler());
                    }
                });

        listenerLoader = new ListenerLoader();
        ListenerLoader.init();
        clientConfig = PropertiesBootStrap.loadClientConfig();

        RpcReference rpcReference = new RpcReference();
        rpcReference.setProxyFactory(new JDKProxyFactory());
        return rpcReference;
    }

    /**
     * 客户端订阅服务
     *
     * @param beanServiceClass 需要订阅的对象
     */
    private void subscribeService(Class<?> beanServiceClass) {
        if (register == null) {
            register = new ZookeeperRegister(clientConfig.getAddress());
        }

        URL url = new URL();
        url.setServiceName(beanServiceClass.getName());
        url.setApplicationName(clientConfig.getApplicationName());
        HashMap<String, String> params = new HashMap<>();
        params.put("host", CommonUtil.getIpAddress());
        url.setParams(params);

        register.subscribe(url);
    }

    /**
     * 根据订阅服务列表拉取所有服务提供者的地址
     */
    public void doConnectServer() {

        // 根据服务列表拉取所有订阅该服务的IP
        for (String serviceName : SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = register.getProviderIps(serviceName);
            for (String providerIp : providerIps) {
                try {
                    ConnectHandler.connect(serviceName, providerIp);
                } catch (Exception e) {
                    logger.info("connect failed...");
                    throw new RuntimeException(e);
                }
            }

            URL url = new URL();
            url.setServiceName(serviceName);

            // 后续监听该服务节点的变化
            register.doAfterSubscribe(url);
        }
    }

    /**
     * 开启异步发送你任务线程
     */
    public void startClientApplication() {
        AsyncRequestTask asyncRequestTask = new AsyncRequestTask();
        new Thread(asyncRequestTask).start();
    }

    static class AsyncRequestTask implements Runnable {

        public AsyncRequestTask() {
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 线程会阻塞在这里
                    RpcInvocation rpcInvocation = REQUEST_QUEUE.take();
                    String json = JSON.toJSONString(rpcInvocation);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());

                    // 根据服务名称获取响应的通信管道
                    ChannelFuture channelFuture = ConnectHandler.getChannelFuture(rpcInvocation.getServiceName());
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {

        // 创建客户端获取代理工厂
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();

        // 获取代理对象，代理对象拦截方法，通过SEND_QUEUE通信
        UserService userService = rpcReference.getProxy(UserService.class);

        // 订阅服务，将信息加入到 SUBSCRIBE_SERVICE_LIST 集合中
        client.subscribeService(UserService.class);
        ConnectHandler.setBootstrap(client.getBootstrap());

        // 拉取订阅的列表，建立netty通信，将信息存入到CONNECT_MAP中
        client.doConnectServer();

        // 开启异步线程，获取SEND_QUEUE中的信息
        client.startClientApplication();

        for (int i = 0; i < 22; i++){
            Thread.sleep(500);
            // 发送请求消息
            System.out.println(userService.getUserInfo("lwj", 1));
        }
    }


    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }
}
