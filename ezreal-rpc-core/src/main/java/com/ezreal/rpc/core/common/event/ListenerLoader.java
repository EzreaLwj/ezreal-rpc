package com.ezreal.rpc.core.common.event;

import com.ezreal.rpc.core.common.event.listener.UpdateServiceListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class ListenerLoader {

    private static List<RpcListener> listeners = new ArrayList<>();

    private static ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public static void init() {
        listeners.add(new UpdateServiceListener());
    }

    public static void registerListener(RpcListener<RpcEvent> listener) {
        listeners.add(listener);
    }

    /**
     * 异步处理事件
     *
     * @param event 事件
     */
    public static void send(RpcEvent event) {
        for (RpcListener listener : listeners) {
            Class<?> eventType = getType(listener);
            if (eventType != null && eventType.isAssignableFrom(event.getClass())) {
                poolExecutor.execute(() -> {
                    try {
                        listener.callback(event.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private static Class<?> getType(RpcListener listener) {

        try {
            // 获取第一个接口类型
            Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
            ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];

            // 获取接口上的泛型
            Type type = parameterizedType.getActualTypeArguments()[0];
            String typeName = type.getTypeName();
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
