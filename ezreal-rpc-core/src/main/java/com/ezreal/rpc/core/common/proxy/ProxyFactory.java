package com.ezreal.rpc.core.common.proxy;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public interface ProxyFactory {

     <T> T getProxy(Class<T> clazz);

}
