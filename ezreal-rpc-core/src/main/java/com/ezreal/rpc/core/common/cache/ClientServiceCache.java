package com.ezreal.rpc.core.common.cache;

import com.ezreal.rpc.core.common.RpcProtocol;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientServiceCache {

    /**
     * 响应信息
     */
    public static final HashMap<String, Object> RESP_MESSAGE = new HashMap<>();

    /**
     * 请求的阻塞队列
     */
    public static final ArrayBlockingQueue<RpcProtocol> REQUEST_QUEUE = new ArrayBlockingQueue<>(100);

}
