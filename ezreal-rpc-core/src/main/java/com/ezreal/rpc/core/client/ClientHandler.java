package com.ezreal.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
        String json = new String(content, 0, rpcProtocol.getContentLength());
        RpcInvocation rpcInvocation = JSON.parseObject(json, RpcInvocation.class);
        if (!RESP_MESSAGE.containsKey(rpcInvocation.getUuid())) {
            throw new RuntimeException("the request is not exist");
        }
        RESP_MESSAGE.put(rpcInvocation.getUuid(),  rpcInvocation);
    }
}
