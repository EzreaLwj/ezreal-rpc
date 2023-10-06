package com.ezreal.rpc.core.router;

import com.ezreal.rpc.core.common.ChannelFuturePollingRef;
import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.register.URL;

import java.util.List;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.*;

/**
 * 轮询路由算法
 * @author Ezreal
 * @Date 2023/10/6
 */
public class RotateRouter implements IRouter{

    @Override
    public void refreshRouteArr(Selector selector) {
        String serviceName = selector.getServiceName();
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(serviceName);
        ChannelFutureWrapper[] channelRoutes = new ChannelFutureWrapper[channelFutureWrappers.size()];

        for (int i = 0; i < channelRoutes.length; i++) {
            channelRoutes[i] = channelFutureWrappers.get(i);
        }

        SERVICE_ROUTE_MAP.put(serviceName, channelRoutes);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getServiceName());
    }

    @Override
    public void updateWeight(URL url) {

    }

}
