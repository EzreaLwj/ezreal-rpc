package com.ezreal.rpc.core.filter.client;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.filter.IClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public class ClientFilterChain {

    private Logger logger = LoggerFactory.getLogger(ClientFilterChain.class);

    private static List<IClientFilter> clientFilterList = new ArrayList<>();

    public void addFilter(IClientFilter clientFilter) {
        clientFilterList.add(clientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        for (IClientFilter clientFilter : clientFilterList) {
            logger.info("调用过滤器, {}", clientFilter.getClass().getName());
            clientFilter.doFilter(src, rpcInvocation);
        }
    }
}
