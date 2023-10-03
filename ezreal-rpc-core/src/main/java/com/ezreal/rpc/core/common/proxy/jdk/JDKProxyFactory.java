package com.ezreal.rpc.core.common.proxy.jdk;

import com.ezreal.rpc.core.common.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class JDKProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Class<T> clazz) {

        return (T) Proxy.newProxyInstance(JDKProxyFactory.class.getClassLoader(),
                new Class[]{clazz},
                new JDKInvocationHandler(clazz));
    }

}
