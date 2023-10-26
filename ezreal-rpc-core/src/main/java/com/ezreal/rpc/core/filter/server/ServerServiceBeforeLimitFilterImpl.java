package com.ezreal.rpc.core.filter.server;

import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.ServerServiceSemaphoreWrapper;
import com.ezreal.rpc.core.common.annotation.SPI;
import com.ezreal.rpc.core.exception.MaxServiceLimitRequestException;
import com.ezreal.rpc.core.filter.IServerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.SERVER_SERVICE_SEMAPHORE_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/25
 */
@SPI("before")
public class ServerServiceBeforeLimitFilterImpl implements IServerFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerServiceBeforeLimitFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {

        String serviceName = rpcInvocation.getServiceName();

        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();

        boolean result = semaphore.tryAcquire();
        if (!result) {
            LOGGER.error("[ServerServiceBeforeLimitFilterImpl] {}'s max request is {},reject now", rpcInvocation.getServiceName(), serverServiceSemaphoreWrapper.getMaxNum());
            MaxServiceLimitRequestException iRpcException = new MaxServiceLimitRequestException(rpcInvocation);
            rpcInvocation.setE(iRpcException);
            throw iRpcException;
        }

    }

}
