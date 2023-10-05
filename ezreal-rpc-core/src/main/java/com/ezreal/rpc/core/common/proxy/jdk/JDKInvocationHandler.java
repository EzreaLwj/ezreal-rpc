package com.ezreal.rpc.core.common.proxy.jdk;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;

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

    private Class<?> targetClass;

    public JDKInvocationHandler(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcInvocation rpcInvocation = new RpcInvocation();

        rpcInvocation.setServiceName(targetClass.getName());
        rpcInvocation.setMethodName(method.getName());
        rpcInvocation.setArgs(args);
        rpcInvocation.setUuid(UUID.randomUUID().toString());

        // 放入请求体
        REQUEST_QUEUE.put(rpcInvocation);
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
