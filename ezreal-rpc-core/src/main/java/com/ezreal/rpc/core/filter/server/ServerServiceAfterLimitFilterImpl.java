package com.ezreal.rpc.core.filter.server;

import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.ServerServiceSemaphoreWrapper;
import com.ezreal.rpc.core.common.annotation.SPI;
import com.ezreal.rpc.core.filter.IServerFilter;

import java.util.concurrent.Semaphore;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.SERVER_SERVICE_SEMAPHORE_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/26
 */
@SPI("after")
public class ServerServiceAfterLimitFilterImpl implements IServerFilter {

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();
        semaphore.release();

    }

}
