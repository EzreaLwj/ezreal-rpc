package com.ezreal.rpc.core.common.event;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public interface RpcListener<T> {
    void callback(Object event);
}
