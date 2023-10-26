package com.ezreal.rpc.core.common.proxy.jdk;

import com.ezreal.rpc.core.client.RpcReferenceWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.REQUEST_QUEUE;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.RESP_MESSAGE;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class JDKInvocationHandler implements InvocationHandler {

    private final Object object = new Object();

    private RpcReferenceWrapper rpcReferenceWrapper;

    private int timeout = 3 * 1000;

    public JDKInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcInvocation rpcInvocation = new RpcInvocation();

        rpcInvocation.setServiceName(rpcReferenceWrapper.getAimClass().getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setArgs(args);
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttachments());
        rpcInvocation.setRetry(rpcReferenceWrapper.getRetry());

        // 放入请求体
        REQUEST_QUEUE.put(rpcInvocation);

        // 既然是一步请求，就没有必要再在RESP_MAP中判断是否有响应结果了
        if (rpcReferenceWrapper.isAsync()) {
            return null;
        }
        RESP_MESSAGE.put(rpcInvocation.getUuid(), object);

        // 接收返回信息
        long currentTimeMillis = System.currentTimeMillis();
        int retryTimes = 0;

        while (System.currentTimeMillis() - currentTimeMillis < timeout || rpcInvocation.getRetry() > 0) {
            Object returnMsg = RESP_MESSAGE.get(rpcInvocation.getUuid());
            if (returnMsg instanceof RpcInvocation) {
                RpcInvocation returnRpcInvocation = (RpcInvocation) returnMsg;
                if (returnRpcInvocation.getRetry() == 0 && returnRpcInvocation.getE() == null) {
                    return ((RpcInvocation) returnMsg).getResponse();
                } else if (returnRpcInvocation.getE() != null){

                    if (returnRpcInvocation.getRetry() == 0) {
                        return returnRpcInvocation.getResponse();
                    }

                    // 大于等待时间才进行重试
                    if (System.currentTimeMillis() - currentTimeMillis > timeout) {
                        retryTimes++;

                        // 重新请求
                        rpcInvocation.setRetry(returnRpcInvocation.getRetry() - 1);
                        rpcInvocation.setResponse(null);
                        REQUEST_QUEUE.put(rpcInvocation);
                        RESP_MESSAGE.put(rpcInvocation.getUuid(), object);
                    }
                }
            }
        }

        throw new TimeoutException("Wait for response from server on client " + timeout + "ms,retry times is " + retryTimes + ",service's name is " + rpcInvocation.getServiceName() + "#" + rpcInvocation.getMethodName());
    }

}
