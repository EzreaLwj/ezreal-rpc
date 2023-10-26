package com.ezreal.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.ezreal.rpc.core.common.cache.ClientServiceCache.CLIENT_SERIALIZE_FACTORY;
import static com.ezreal.rpc.core.common.cache.ClientServiceCache.RESP_MESSAGE;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = (RpcProtocol) msg;

        byte[] content = rpcProtocol.getContent();
        RpcInvocation rpcInvocation = CLIENT_SERIALIZE_FACTORY.deserialize(content,RpcInvocation.class);
        if (rpcInvocation.getE() != null) {
            rpcInvocation.getE().printStackTrace();
        }
        // 如果是单纯异步模式的话，响应Map集合中不会存在映射值
        Object r = rpcInvocation.getAttachments().get("async");
        if (r != null && Boolean.valueOf(String.valueOf(r))) {

            // 释放netty的直接内存(堆外内存)
            ReferenceCountUtil.release(msg);
            return;
        }
        if (!RESP_MESSAGE.containsKey(rpcInvocation.getUuid())) {
            throw new RuntimeException("the request is not exist");
        }
        RESP_MESSAGE.put(rpcInvocation.getUuid(),  rpcInvocation);

        // 释放netty的直接内存(堆外内存)
        ReferenceCountUtil.release(msg);
    }
}
