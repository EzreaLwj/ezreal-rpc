package com.ezreal.rpc.core.common.proxy.jdk;

import com.ezreal.rpc.core.client.RpcReferenceWrapper;
import com.ezreal.rpc.core.common.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class JDKProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) {
        Class<T> clazz = rpcReferenceWrapper.getAimClass();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new JDKInvocationHandler(rpcReferenceWrapper));
    }

}
