package com.ezreal.rpc.core.register;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public interface RegistryService {

    /**
     * 向注册中心注册服务
     * @param url 服务地址
     */
    void register(URL url);

    /**
     * 向注册中心移除注册服务
     * @param url 服务地址
     */
    void unRegister(URL url);

    /**
     * 消费者订阅服务端的列表
     */
    void subscribe(URL url);

    /**
     * 消费者取消订阅
     */
    void doUnScribe(URL url);
}
