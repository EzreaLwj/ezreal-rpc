package com.ezreal.rpc.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.ezreal.rpc.core.common.constants.RpcConstants.MAGIC_NUMBER;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                channelHandlerContext.close();
                return;
            }

            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {

                // 数据包有异常
                channelHandlerContext.close();
                return;
            }

            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            RpcProtocol rpcProtocol = new RpcProtocol(body);
            list.add(rpcProtocol);
        }
    }

}
