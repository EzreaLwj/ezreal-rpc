package com.ezreal.rpc.core.common.cache;

import com.ezreal.rpc.core.common.ServerServiceSemaphoreWrapper;
import com.ezreal.rpc.core.dispatcher.ServerChannelDispatcher;
import com.ezreal.rpc.core.filter.server.ServerAfterFilterChain;
import com.ezreal.rpc.core.filter.server.ServerBeforeFilterChain;
import com.ezreal.rpc.core.register.RegistryService;
import com.ezreal.rpc.core.register.URL;
import com.ezreal.rpc.core.serialize.SerializeFactory;
import com.ezreal.rpc.core.server.ServiceWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class ServerServiceCache {

    /**
     * 服务名->暴露对象
     */
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();

    /**
     * 服务提供者列表
     */
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();

    public static RegistryService REGISTRY_SERVICE;

    public static SerializeFactory SERVER_SERIALIZE_FACTORY;

    /**
     * 服务信息wrapper类
     */
    public static final Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();

    /**
     * 服务端前执行链
     */
    public static ServerAfterFilterChain SERVER_AFTER_FILTER_CHAIN;

    /**
     * 服务端后执行链
     */
    public static ServerBeforeFilterChain SERVER_BEFORE_FILTER_CHAIN;

    public static ServerChannelDispatcher SERVER_CHANNEL_DISPATCHER = new ServerChannelDispatcher();

    public static final Map<String, ServerServiceSemaphoreWrapper> SERVER_SERVICE_SEMAPHORE_MAP = new ConcurrentHashMap<>(64);

}
