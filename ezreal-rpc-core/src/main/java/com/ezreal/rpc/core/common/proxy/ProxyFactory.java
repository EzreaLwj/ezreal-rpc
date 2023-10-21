package com.ezreal.rpc.core.common.proxy;

import com.ezreal.rpc.core.client.RpcReferenceWrapper;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public interface ProxyFactory {

     <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper);

}
