package com.ezreal.rpc.core.client;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.ProviderNodeInfo;
import com.ezreal.rpc.core.router.Selector;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.Arrays;
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
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        ChannelFuture channelFuture = bootstrap.connect(host, port);

        String providerUrlInfo = URL_MAP.get(serviceName).get(address);
        ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(providerUrlInfo);
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setHost(host);
        channelFutureWrapper.setPort(port);
        channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setGroup(providerNodeInfo.getGroup());

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
     * 默认走随机策略获取ChannelFuture
     *
     * @param rpcInvocation
     * @return
     */
    public static ChannelFuture getChannelFuture(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getServiceName();
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTE_MAP.get(serviceName);
        if (channelFutureWrappers == null || channelFutureWrappers.length == 0) {
            throw new RuntimeException("no provider exist for " + serviceName);
        }
        CLIENT_FILTER_CHAIN.doFilter(Arrays.asList(channelFutureWrappers),rpcInvocation);
        Selector selector = new Selector();
        selector.setServiceName(serviceName);
        selector.setChannelFutureWrappers(channelFutureWrappers);
        return I_ROUTER.select(selector).getChannelFuture();
    }
}
