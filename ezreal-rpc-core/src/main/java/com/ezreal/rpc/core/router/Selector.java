package com.ezreal.rpc.core.router;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;

/**
 * 路由选择器
 * @author Ezreal
 * @Date 2023/10/6
 */
public class Selector {

    /**
     * 服务名 com.ezreal.rpc.core.
     */
    private String serviceName;

    /**
     * 二次筛选后的future集合
     */
    private ChannelFutureWrapper[] channelFutureWrappers;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ChannelFutureWrapper[] getChannelFutureWrappers() {
        return channelFutureWrappers;
    }

    public void setChannelFutureWrappers(ChannelFutureWrapper[] channelFutureWrappers) {
        this.channelFutureWrappers = channelFutureWrappers;
    }
}
