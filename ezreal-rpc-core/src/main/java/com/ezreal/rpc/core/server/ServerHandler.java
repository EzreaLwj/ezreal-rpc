package com.ezreal.rpc.core.server;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.*;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);

        // 执行过滤器链
        SERVER_FILTER_CHAIN.doFilter(rpcInvocation);

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
        RpcProtocol responseRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
        ctx.writeAndFlush(responseRpcProtocol);
    }

}
