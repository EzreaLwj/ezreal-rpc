package com.ezreal.rpc.core.client;

import com.ezreal.rpc.core.common.proxy.ProxyFactory;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class RpcReference {

    private ProxyFactory proxyFactory;

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(RpcReferenceWrapper<T> rpcReferenceWrapper) {
        return proxyFactory.getProxy(rpcReferenceWrapper);
    }

}
