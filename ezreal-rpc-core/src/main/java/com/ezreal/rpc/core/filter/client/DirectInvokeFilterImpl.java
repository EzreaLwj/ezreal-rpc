package com.ezreal.rpc.core.filter.client;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.filter.IClientFilter;

import java.util.Iterator;
import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public class DirectInvokeFilterImpl implements IClientFilter {

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {

        String url = (String) rpcInvocation.getAttachments().get("url");
        if (CommonUtil.isEmpty(url)) {
            return;
        }

        Iterator<ChannelFutureWrapper> iterator = src.iterator();
        while (iterator.hasNext()) {
            ChannelFutureWrapper channelFutureWrapper = iterator.next();
            String host = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if (!url.equals(host)) {
                iterator.remove();
            }
        }

        if (CommonUtil.isEmpty(src)) {
            throw new RuntimeException("no match provider url for " + url);
        }
    }

}
