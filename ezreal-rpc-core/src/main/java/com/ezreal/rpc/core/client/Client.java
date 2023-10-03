package com.ezreal.rpc.core.client;

import com.ezreal.rpc.core.common.RpcDecoder;
import com.ezreal.rpc.core.common.RpcEncoder;
import com.ezreal.rpc.core.common.RpcProtocol;
import com.ezreal.rpc.core.common.RpcReference;
import com.ezreal.rpc.core.common.config.ClientConfig;
import com.ezreal.rpc.core.common.proxy.jdk.JDKProxyFactory;
import com.ezreal.rpc.core.common.test.TestService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.REQUEST_QUEUE;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class Client {

    private final Logger logger = LoggerFactory.getLogger(Client.class);

    private ClientConfig clientConfig;

    private NioEventLoopGroup eventExecutors;

    public Client() {
    }

    public Client(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference startClientApplication() throws InterruptedException {
        eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
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

        logger.info("客户端启动,连接端口:{}", clientConfig.getPort());
        ChannelFuture channelFuture = bootstrap.connect(clientConfig.getHost(), clientConfig.getPort()).sync();

        // 这里要启动一个线程来发送消息
        startAsyncRequestSender(channelFuture);

        RpcReference rpcReference = new RpcReference();
        rpcReference.setProxyFactory(new JDKProxyFactory());
        return rpcReference;
    }

    private void startAsyncRequestSender(ChannelFuture channelFuture) {
        AsyncRequestTask asyncRequestTask = new AsyncRequestTask(channelFuture);
        new Thread(asyncRequestTask).start();
    }

    static class AsyncRequestTask implements Runnable {

        private ChannelFuture channelFuture;

        public AsyncRequestTask(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 线程会阻塞在这里
                    RpcProtocol rpcProtocol = REQUEST_QUEUE.take();
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig("localhost", 9999);
        Client client = new Client(config);
        RpcReference rpcReference = client.startClientApplication();
        TestService proxy = rpcReference.getProxy(TestService.class);
        System.out.println(proxy.sayName("lwj", 1));
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
