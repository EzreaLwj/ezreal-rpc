package com.ezreal.rpc.core.filter.client;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.filter.IClientFilter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public class ClientGroupFilterImpl implements IClientFilter {

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {

        String groupName = String.valueOf(rpcInvocation.getAttachments().get("group"));

        Iterator<ChannelFutureWrapper> channelFutureWrapperIterator = src.iterator();
        while (channelFutureWrapperIterator.hasNext()) {
            ChannelFutureWrapper channelFutureWrapper = channelFutureWrapperIterator.next();
            if (!channelFutureWrapper.getGroup().equals(groupName)) {
                channelFutureWrapperIterator.remove();
            }
        }

        if (CommonUtil.isEmpty(src)) {
            throw new RuntimeException("no provider match for group " + groupName);
        }
    }

}
