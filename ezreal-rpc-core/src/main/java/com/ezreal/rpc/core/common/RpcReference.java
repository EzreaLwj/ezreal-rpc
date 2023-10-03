package com.ezreal.rpc.core.common;

import com.ezreal.rpc.core.common.proxy.ProxyFactory;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class RpcReference {

    private ProxyFactory proxyFactory;


    public <T> T getProxy(Class<T> targetClass) {
        return proxyFactory.getProxy(targetClass);
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

}
