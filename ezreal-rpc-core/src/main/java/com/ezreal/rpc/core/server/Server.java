package com.ezreal.rpc.core.server;

import com.ezreal.rpc.core.common.RpcDecoder;
import com.ezreal.rpc.core.common.RpcEncoder;
import com.ezreal.rpc.core.common.config.ServerConfig;
import com.ezreal.rpc.core.common.test.TestServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import static com.ezreal.rpc.core.common.cache.ServerServiceCache.PROVIDER_CLASS_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class Server {

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private ServerConfig serverConfig;

    private EventLoopGroup bossEventLoopGroup;

    private EventLoopGroup workerEventLoopGroup;

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
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
        ChannelFuture channelFuture = serverBootstrap.bind(serverConfig.getPort()).sync();
        channelFuture.channel().closeFuture().sync();
    }

    public void registerService(Object beanService) {
        Class<?>[] interfaces = beanService.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RuntimeException("the object must have one interface");
        }
        if (interfaces.length >1) {
            throw new RuntimeException("the object only has one interface");
        }
        Class<?> anInterface = interfaces[0];
        PROVIDER_CLASS_MAP.put(anInterface.getName(), beanService);
    }


    public static void main(String[] args) throws InterruptedException {
        ServerConfig serverConfig = new ServerConfig(9999);
        Server server = new Server();
        server.setServerConfig(serverConfig);
        server.registerService(new TestServiceImpl());
        server.setOnApplication();
    }
}
