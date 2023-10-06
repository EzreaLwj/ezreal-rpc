package com.ezreal.rpc.core.common;

import java.util.concurrent.atomic.AtomicInteger;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.SERVICE_ROUTE_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/6
 */
public class ChannelFuturePollingRef {

    private AtomicInteger atomicLong = new AtomicInteger(0);

    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName) {
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTE_MAP.get(serviceName);
        int len = channelFutureWrappers.length;
        int incr = atomicLong.incrementAndGet();
        return channelFutureWrappers[incr % len];
    }
}
