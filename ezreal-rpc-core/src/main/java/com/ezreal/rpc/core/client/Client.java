package com.ezreal.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.*;
import com.ezreal.rpc.core.common.config.ClientConfig;
import com.ezreal.rpc.core.common.config.PropertiesBootStrap;
import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.proxy.jdk.JDKProxyFactory;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.filter.client.ClientFilterChain;
import com.ezreal.rpc.core.filter.client.ClientGroupFilterImpl;
import com.ezreal.rpc.core.filter.client.ClientLogFilterImpl;
import com.ezreal.rpc.core.filter.client.DirectInvokeFilterImpl;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.AbstractRegister;
import com.ezreal.rpc.core.register.zookeeper.ZookeeperRegister;
import com.ezreal.rpc.core.router.RandomRouter;
import com.ezreal.rpc.core.router.RotateRouter;
import com.ezreal.rpc.core.serialize.fastjson.FastJsonSerializeFactory;
import com.ezreal.rpc.core.serialize.jdk.JDKSerializeFactory;
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
import java.util.Map;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.*;
import static com.ezreal.rpc.core.common.constants.RpcConstants.*;

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
        listenerLoader.init();

        RpcReference rpcReference = new RpcReference();
        rpcReference.setProxyFactory(new JDKProxyFactory());
        return rpcReference;
    }

    /**
     * 初始化客户端配置文件
     */
    private void initClientConfig() {
        clientConfig = PropertiesBootStrap.loadClientConfig();
        if (RANDOM_ROUTER_TYPE.equals(clientConfig.getRouterStrategy())) {
            I_ROUTER = new RandomRouter();
        } else if(ROTATE_ROUTER_TYPE.equals(clientConfig.getRouterStrategy())) {
            I_ROUTER = new RotateRouter();
        }
        CLIENT_CONFIG = clientConfig;

        String clientSerialize = clientConfig.getClientSerialize();
        switch (clientSerialize) {
            case JDK_SERIALIZE_TYPE:
                CLIENT_SERIALIZE_FACTORY = new JDKSerializeFactory();
                break;
            case FAST_JSON_SERIALIZE_TYPE:
                CLIENT_SERIALIZE_FACTORY = new FastJsonSerializeFactory();
                break;
            default:
                CLIENT_SERIALIZE_FACTORY = new JDKSerializeFactory();
                break;
        }

        ClientFilterChain clientFilterChain = new ClientFilterChain();
        clientFilterChain.addFilter(new ClientGroupFilterImpl());
        clientFilterChain.addFilter(new ClientLogFilterImpl());
        clientFilterChain.addFilter(new DirectInvokeFilterImpl());
        CLIENT_FILTER_CHAIN = clientFilterChain;
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

        String serviceName = beanServiceClass.getName();
        URL url = new URL();
        url.setServiceName(serviceName);
        url.setApplicationName(clientConfig.getApplicationName());
        HashMap<String, String> params = new HashMap<>();
        params.put("host", CommonUtil.getIpAddress());
        url.setParams(params);

        // 获取服务的权重
        Map<String, String> weightMap = register.getServiceWeightMap(serviceName);
        URL_MAP.put(serviceName, weightMap);
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
            HashMap<String, String> params = new HashMap<>();
            params.put("providerPath", serviceName + "/provider");
            params.put("providerIps", JSON.toJSONString(providerIps));
            url.setParams(params);
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
                    RpcProtocol rpcProtocol = new RpcProtocol(CLIENT_SERIALIZE_FACTORY.serialize(rpcInvocation));

                    // 根据服务名称获取响应的通信管道
                    ChannelFuture channelFuture = ConnectHandler.getChannelFuture(rpcInvocation);
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

        for (int i = 0; i < 22; i++){
            Thread.sleep(500);
            // 发送请求消息
            System.out.println(userService.getUserInfo("lwj", i));
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
