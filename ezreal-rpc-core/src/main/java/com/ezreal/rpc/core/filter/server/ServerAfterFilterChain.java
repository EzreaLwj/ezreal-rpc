package com.ezreal.rpc.core.filter.server;

import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.filter.IServerFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/26
 */
public class ServerAfterFilterChain {

    private static List<IServerFilter> serverFilterList = new ArrayList<>();

    public void addServerFilter(IServerFilter serverFilter) {
        serverFilterList.add(serverFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation) {
        for (IServerFilter serverFilter : serverFilterList) {
            serverFilter.doFilter(rpcInvocation);
        }
    }
}
