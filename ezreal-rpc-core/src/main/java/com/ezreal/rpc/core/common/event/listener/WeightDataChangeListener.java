package com.ezreal.rpc.core.common.event.listener;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.event.RpcListener;
import com.ezreal.rpc.core.common.event.WeightDataChangeEvent;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.register.zookeeper.ProviderNodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.CONNECT_MAP;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.I_ROUTER;

/**
 * @author Ezreal
 * @Date 2023/10/6
 */
public class WeightDataChangeListener implements RpcListener<WeightDataChangeEvent> {

    private final Logger logger = LoggerFactory.getLogger(WeightDataChangeListener.class);

    @Override
    public void callback(Object event) {

        logger.info("收到服务器的变化...");
        ProviderNodeInfo providerNodeInfo = (ProviderNodeInfo) event;

        String serviceName = providerNodeInfo.getServiceName();
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);

        // 找到对应的Channel修改它的权重, 然后重新求概率
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String address = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if (address.equals(providerNodeInfo.getAddress())) {
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());

                URL url = new URL();
                url.setServiceName(providerNodeInfo.getServiceName());
                I_ROUTER.updateWeight(url);
                break;
            }
        }
    }

}
