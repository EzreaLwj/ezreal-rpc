package com.ezreal.rpc.core.common.event.listener;

import com.ezreal.rpc.core.common.event.RpcListener;
import com.ezreal.rpc.core.common.event.ServiceDestroyEvent;
import com.ezreal.rpc.core.register.URL;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.PROVIDER_URL_SET;
import static com.ezreal.rpc.core.common.cache.ServerServiceCache.REGISTRY_SERVICE;

/**
 * @author Ezreal
 * @Date 2023/10/6
 */
public class ServiceDestroyListener implements RpcListener<ServiceDestroyEvent> {

    @Override
    public void callback(Object event) {
        for (URL url : PROVIDER_URL_SET) {
            REGISTRY_SERVICE.unRegister(url);
        }
    }

}
