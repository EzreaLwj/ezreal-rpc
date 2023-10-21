package com.ezreal.rpc.core.filter;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;

import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public interface IClientFilter extends IFilter{

    /**
     * 执行过滤器链
     * @param src
     * @param rpcInvocation 数据对象
     */
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
