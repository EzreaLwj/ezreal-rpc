package com.ezreal.rpc.core.client;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.router.Selector;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.*;

/**
 * @author Ezreal
 * @Date 2023/10/5
 */
public class ConnectHandler {

    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectHandler.bootstrap = bootstrap;
    }

    public static void connect(String serviceName, String address) {
        if (!address.contains(":")) {
            throw new RuntimeException("address format error...");
        }

        if (CommonUtil.isEmpty(serviceName)) {
            throw new RuntimeException("serviceName should not be null...");
        }

        String[] split = address.split(":");
        ChannelFuture channelFuture = bootstrap.connect(split[0], Integer.valueOf(split[1]));

        String providerUrlInfo = URL_MAP.get(serviceName).get(address);
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setHost(split[0]);
        channelFutureWrapper.setPort(Integer.valueOf(split[1]));
        channelFutureWrapper.setWeight(Integer.valueOf(providerUrlInfo.substring(providerUrlInfo.lastIndexOf(";")+1)));
        channelFutureWrapper.setChannelFuture(channelFuture);

        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if (CommonUtil.isEmpty(channelFutureWrappers)) {
            channelFutureWrappers = new ArrayList<>();

        }
        channelFutureWrappers.add(channelFutureWrapper);
        PROVIDERS.add(address);
        CONNECT_MAP.put(serviceName, channelFutureWrappers);

        // 根据路由数组生成策略
        Selector selector = new Selector();
        selector.setServiceName(serviceName);
        I_ROUTER.refreshRouteArr(selector);
    }

    public static ChannelFuture createChannelFuture(String host, Integer port) {
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        return channelFuture;
    }

    /**
     * 根据服务名称获取对应的 channel
     * @param serviceName
     * @return
     */
    public static ChannelFuture getChannelFuture(String serviceName) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if (channelFutureWrappers == null) {
            throw new RuntimeException("the channelFutures is not exist , serviceName: " + serviceName);
        }

        Selector selector = new Selector();
        selector.setServiceName(serviceName);
        ChannelFutureWrapper channelFutureWrapper = I_ROUTER.select(selector);

        System.out.println("调用信息为: host: " + channelFutureWrapper.getHost() + " port:" + channelFutureWrapper.getPort());
        return channelFutureWrapper.getChannelFuture();
    }
}
