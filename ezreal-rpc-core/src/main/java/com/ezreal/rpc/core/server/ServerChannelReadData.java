package com.ezreal.rpc.core.server;

import com.ezreal.rpc.core.common.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Ezreal
 * @Date 2023/10/23
 */
public class ServerChannelReadData {

    private RpcProtocol protocol;

    private ChannelHandlerContext channelHandlerContext;

    public ServerChannelReadData() {
    }

    public RpcProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(RpcProtocol protocol) {
        this.protocol = protocol;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
