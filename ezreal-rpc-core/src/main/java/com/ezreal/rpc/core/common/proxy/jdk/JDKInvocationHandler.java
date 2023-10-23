package com.ezreal.rpc.core.common.proxy.jdk;

import com.ezreal.rpc.core.client.RpcReferenceWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.REQUEST_QUEUE;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.RESP_MESSAGE;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class JDKInvocationHandler implements InvocationHandler {

    private final Object object = new Object();

    private RpcReferenceWrapper rpcReferenceWrapper;

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

        // 放入请求体
        REQUEST_QUEUE.put(rpcInvocation);

        // 既然是一步请求，就没有必要再在RESP_MAP中判断是否有响应结果了
        if (rpcReferenceWrapper.isAsync()) {
            return null;
        }
        RESP_MESSAGE.put(rpcInvocation.getUuid(), object);

        // 接收返回信息
        long currentTimeMillis = System.currentTimeMillis();

        while (System.currentTimeMillis() - currentTimeMillis < 3 * 1000) {
            Object returnMsg = RESP_MESSAGE.get(rpcInvocation.getUuid());
            if (returnMsg instanceof RpcInvocation) {
                return ((RpcInvocation) returnMsg).getResponse();
            }
        }

        throw new RuntimeException("response timeout");
    }

}
