package com.ezreal.rpc.core.filter.server;

import com.ezreal.rpc.core.common.ChannelFutureWrapper;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.annotation.SPI;
import com.ezreal.rpc.core.common.utils.CommonUtil;
import com.ezreal.rpc.core.filter.IServerFilter;
import com.ezreal.rpc.core.server.ServiceWrapper;

import java.util.List;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.PROVIDER_SERVICE_WRAPPER_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
@SPI("before")
public class ServerTokenFilterImpl implements IServerFilter {

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getServiceName();
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(serviceName);

        // 本身没有设置token就直接退出
        String matchToken = serviceWrapper.getServiceToken();
        if (CommonUtil.isEmpty(matchToken)) {
            return;
        }

        // 如果设置token就进行比较
        String token = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        if (!CommonUtil.isEmpty(token) && token.equals(matchToken)) {
            return;
        }

        throw new RuntimeException(String.format("the token is not match, token: %s, matchToken: %s", token, matchToken));
    }

}
