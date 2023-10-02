package com.ezreal.rpc.core.server;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.PROVIDER_CLASS_MAP;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String json = new String(rpcProtocol.getContent(), 0, rpcProtocol.getContentLength());
        RpcInvocation rpcInvocation = JSON.parseObject(json, RpcInvocation.class);

        String serviceName = rpcInvocation.getServiceName();
        Object beanService = PROVIDER_CLASS_MAP.get(serviceName);

        Method[] methods = beanService.getClass().getMethods();
        Object result = null;
        for (Method method : methods) {
            if (method.getName().equals(rpcInvocation.getMethodName())) {
                if (method.getReturnType().equals(Void.class)) {
                    method.invoke(beanService, rpcInvocation.getArgs());
                } else {
                    result = method.invoke(beanService, rpcInvocation.getArgs());
                }
            }
        }

        rpcInvocation.setResponse(result);
        RpcProtocol responseRpcProtocol = new RpcProtocol(JSON.toJSONString(rpcInvocation).getBytes());
        ctx.writeAndFlush(responseRpcProtocol);
    }

}
