package com.ezreal.rpc.core.common.cache;

import com.ezreal.rpc.core.common.ChannelFuturePollingRef;
import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import com.ezreal.rpc.core.router.IRouter;
import com.ezreal.rpc.core.serialize.SerializeFactory;

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

    //com.sise.test.service -> <<ip:host,urlString>,<ip:host,urlString>,<ip:host,urlString>>
    public static Map<String, Map<String,String>> URL_MAP = new ConcurrentHashMap<>();

    /**
     * 服务名称->路由 channel数组
     */
    public static final Map<String, ChannelFutureWrapper[]> SERVICE_ROUTE_MAP = new HashMap<>();

    public static final ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();

    public static IRouter I_ROUTER;

    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;

}
