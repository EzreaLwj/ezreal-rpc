package com.ezreal.rpc.core.register.zookeeper;

import com.ezreal.rpc.core.register.RegistryService;
import com.ezreal.rpc.core.register.URL;

import java.util.List;
import java.util.Map;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.SUBSCRIBE_SERVICE_LIST;
import static com.ezreal.rpc.core.common.cache.ServerServiceCache.PROVIDER_URL_SET;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public abstract class AbstractRegister implements RegistryService {

    @Override
    public void register(URL url) {
        PROVIDER_URL_SET.add(url);
    }

    @Override
    public void unRegister(URL url) {
        PROVIDER_URL_SET.remove(url);
    }

    @Override
    public void subscribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.add(url.getServiceName());
    }

    @Override
    public void doUnScribe(URL url) {
        SUBSCRIBE_SERVICE_LIST.remove(url.getServiceName());
    }

    /**
     * 订阅前扩展
     */
    public abstract void doBeforeSubscribe(URL url);

    /**
     * 订阅后扩展
     */
    public abstract void doAfterSubscribe(URL url);


    /**
     * 留给子类扩展
     *
     * @param serviceName
     * @return
     */
    public abstract List<String> getProviderIps(String serviceName);


    /**
     * 获取服务的权重信息
     *
     * @param serviceName
     * @return <ip:port --> urlString>,<ip:port --> urlString>,<ip:port --> urlString>,<ip:port --> urlString>
     */
    public abstract Map<String, String> getServiceWeightMap(String serviceName);

}
