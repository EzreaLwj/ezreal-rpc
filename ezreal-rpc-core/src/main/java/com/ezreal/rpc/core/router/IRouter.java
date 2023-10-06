package com.ezreal.rpc.core.router;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.register.URL;

/**
 * @author Ezreal
 * @Date 2023/10/6
 */
public interface IRouter {

    /**
     * 更新路由信息
     * @param selector 路由选择器
     */
    void refreshRouteArr(Selector selector);

    /**
     * 选择具体的路由
     * @param selector 路由选择器
     * @return 通道
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新路由权重
     * @param url url
     */
    void updateWeight(URL url);

}
