package com.ezreal.rpc.core.common.event.listener;

import com.ezreal.rpc.core.client.ConnectHandler;
import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.event.RpcListener;
import com.ezreal.rpc.core.common.event.UpdateServiceEvent;
import com.ezreal.rpc.core.common.event.data.URLChangeWrapper;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.CONNECT_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class UpdateServiceListener implements RpcListener<UpdateServiceEvent> {

    private final Logger logger = LoggerFactory.getLogger(UpdateServiceListener.class);

    @Override
    public void callback(Object data) {
        URLChangeWrapper wrapper = (URLChangeWrapper) data;

        // 目前存活的服务(包括新的和旧的)
        List<String> providerUrls = wrapper.getProviderUrl();
        String serviceName = wrapper.getServiceName();

        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        if (CommonUtil.isEmpty(channelFutureWrappers)) {
            logger.error("the channelFutureWrappers is empty");
            return;
        } else {
            List<ChannelFutureWrapper> fianlChannelFutureWrappers = new ArrayList<>();
            HashSet<String> set = new HashSet<>();
            // 移除旧的服务
            for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
                String host = channelFutureWrapper.getHost();
                Integer port = channelFutureWrapper.getPort();

                String serverHost = host + ":" + port;
                if (!providerUrls.contains(serverHost)) {
                    continue;
                } else {
                    set.add(serverHost);
                    fianlChannelFutureWrappers.add(channelFutureWrapper);
                }
            }

            List<ChannelFutureWrapper> newChannelFutureWrappers = new ArrayList<>();
            // 添加新的服务,新的服务需要重新建立连接
            for (String providerUrl : providerUrls) {
                if (!set.contains(providerUrl)) {
                    ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
                    String[] split = providerUrl.split(":");
                    String host = split[0];
                    Integer port = Integer.valueOf(split[1]);
                    channelFutureWrapper.setHost(host);
                    channelFutureWrapper.setPort(port);
                    try {
                        ChannelFuture channelFuture = ConnectHandler.createChannelFuture(host, port);
                        channelFutureWrapper.setChannelFuture(channelFuture);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    set.add(providerUrl);
                    newChannelFutureWrappers.add(channelFutureWrapper);
                }
            }

            fianlChannelFutureWrappers.addAll(newChannelFutureWrappers);
            CONNECT_MAP.put(serviceName, fianlChannelFutureWrappers);
        }
    }

}
