package com.ezreal.rpc.core.common.cache;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientServiceCache {

    /**
     * 响应信息
     */
    public static final Map<String, Object> RESP_MESSAGE = new ConcurrentHashMap<>();

    /**
     * 请求的阻塞队列
     */
    public static final ArrayBlockingQueue<RpcInvocation> REQUEST_QUEUE = new ArrayBlockingQueue<>(100);

    /**
     * 客户端订阅服务的列表
     */
    public static List<String> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    /**
     * 远程服务的列表 服务名称->Socket连接
     */
    public static final Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    /**
     * 服务Set, 存放相应的IP
     */
    public static final Set<String> PROVIDERS = new HashSet<>();

}
