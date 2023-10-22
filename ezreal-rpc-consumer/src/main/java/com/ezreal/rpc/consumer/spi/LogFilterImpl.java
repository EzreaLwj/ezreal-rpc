package com.ezreal.rpc.consumer.spi;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.filter.IClientFilter;

import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/22
 */
public class LogFilterImpl implements IClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        System.out.println("this is a test");
    }
}
