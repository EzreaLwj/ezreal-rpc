package com.ezreal.rpc.core.filter.server;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.filter.IServerFilter;

import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public class ServerLogFilterImpl implements IServerFilter {

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        System.out.println(rpcInvocation.getAttachments().get("c_app_name") + " do invoke -----> " + rpcInvocation.getServiceName() + "#" + rpcInvocation.getMethodName());
    }

}
