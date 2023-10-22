package com.ezreal.rpc.core.server;

import com.ezreal.rpc.core.common.RpcDecoder;
import com.ezreal.rpc.core.common.RpcEncoder;
import com.ezreal.rpc.core.common.config.PropertiesBootStrap;
import com.ezreal.rpc.core.common.config.ServerConfig;
import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.filter.IServerFilter;
import com.ezreal.rpc.core.filter.server.ServerFilterChain;
import com.ezreal.rpc.core.filter.server.ServerLogFilterImpl;
import com.ezreal.rpc.core.filter.server.ServerTokenFilterImpl;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.AbstractRegister;
import com.ezreal.rpc.core.register.zookeeper.ZookeeperRegister;
import com.ezreal.rpc.core.serialize.SerializeFactory;
import com.ezreal.rpc.core.serialize.fastjson.FastJsonSerializeFactory;
import com.ezreal.rpc.core.serialize.jdk.JDKSerializeFactory;
import com.ezreal.rpc.core.spi.ExtensionLoader;
import com.ezreal.rpc.test.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.CLIENT_SERIALIZE_FACTORY;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.EXTENSION_LOADER;
import static com.ezreal.rpc.core.common.cache.ServerServiceCache.*;
import static com.ezreal.rpc.core.common.constants.RpcConstants.FAST_JSON_SERIALIZE_TYPE;
import static com.ezreal.rpc.core.common.constants.RpcConstants.JDK_SERIALIZE_TYPE;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class Server {

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConfig serverConfig;

    private EventLoopGroup bossEventLoopGroup;

    private EventLoopGroup workerEventLoopGroup;


    private static ListenerLoader listenerLoader;

    public void initServerConfig() throws Exception {

        this.serverConfig = PropertiesBootStrap.loadServerConfig();

        EXTENSION_LOADER.load(SerializeFactory.class);
        LinkedHashMap<String, Class<?>> serializeFactoryLinkedHashMap = ExtensionLoader.CLASS_CACHE.get(SerializeFactory.class.getName());
        String serverSerialize = serverConfig.getServerSerialize();
        Class<?> serializeFactoryClass = serializeFactoryLinkedHashMap.get(serverSerialize);
        SERVER_SERIALIZE_FACTORY = (SerializeFactory) serializeFactoryClass.newInstance();

        EXTENSION_LOADER.load(IServerFilter.class);
        LinkedHashMap<String, Class<?>> serverFilterLinkedHashMap = ExtensionLoader.CLASS_CACHE.get(IServerFilter.class.getName());
        Set<String> filterName = serverFilterLinkedHashMap.keySet();
        ServerFilterChain serverFilterChain = new ServerFilterChain();
        for (String name : filterName) {
            Class<?> aClass = serverFilterLinkedHashMap.get(name);
            serverFilterChain.addServerFilter((IServerFilter) aClass.newInstance());
        }

        SERVER_FILTER_CHAIN = serverFilterChain;
    }

    public void setOnApplication() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, 1024 * 16)
                .option(ChannelOption.SO_RCVBUF, 1024 * 16)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new RpcEncoder());
                        channel.pipeline().addLast(new RpcDecoder());
                        channel.pipeline().addLast(new ServerHandler());
                    }
                });

        logger.info("服务端启动,监听端口{}", serverConfig.getPort());
        this.batchExport();
        ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
        channelFuture.channel().closeFuture().sync();
    }

    public void exportService(ServiceWrapper serviceWrapper) {
        Object beanService = serviceWrapper.getServiceObj();
        Class<?>[] interfaces = beanService.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RuntimeException("the object must have one interface");
        }
        if (interfaces.length > 1) {
            throw new RuntimeException("the object only has one interface");
        }

        if (REGISTRY_SERVICE == null) {
            REGISTRY_SERVICE = new ZookeeperRegister(serverConfig.getAddress());
        }

        // 默认实现第一个接口
        Class<?> anInterface = interfaces[0];
        String serviceName = anInterface.getName();
        PROVIDER_CLASS_MAP.put(serviceName, beanService);

        URL url = new URL();
        url.setServiceName(serviceName);
        url.setApplicationName(serverConfig.getApplicationName());
        HashMap<String, String> params = new HashMap<>();
        params.put("host", CommonUtil.getIpAddress());
        params.put("port", String.valueOf(serverConfig.getPort()));
        params.put("limit", String.valueOf(serviceWrapper.getLimit()));
        params.put("group", String.valueOf(serviceWrapper.getGroup()));

        url.setParams(params);
        PROVIDER_URL_SET.add(url);

        if (!CommonUtil.isEmpty(serviceWrapper.getServiceToken())) {
            PROVIDER_SERVICE_WRAPPER_MAP.put(serviceName, serviceWrapper);
        }
    }

    public void batchExport() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (URL url : PROVIDER_URL_SET) {
                    REGISTRY_SERVICE.register(url);
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {

        // 初始化配置
        Server server = new Server();
        server.initServerConfig();

        // 配置事件监听
        listenerLoader = new ListenerLoader();
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

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

}
