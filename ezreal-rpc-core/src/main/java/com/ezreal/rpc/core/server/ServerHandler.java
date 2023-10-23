package com.ezreal.rpc.core.server;

import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.SERVER_CHANNEL_DISPATCHER;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcProtocol rpcProtocol = (RpcProtocol) msg;

        ServerChannelReadData serverChannelReadData = new ServerChannelReadData();
        serverChannelReadData.setChannelHandlerContext(ctx);
        serverChannelReadData.setProtocol(rpcProtocol);

        SERVER_CHANNEL_DISPATCHER.add(serverChannelReadData);
    }

}
