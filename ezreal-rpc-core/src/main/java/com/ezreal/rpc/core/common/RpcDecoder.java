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
            // 防止收到一些体积过大的数据包
            if (byteBuf.readableBytes() > 1000) {
                byteBuf.skipBytes(byteBuf.readableBytes());
            }

            int readIndex;
            while (true) {
                // 标志当前读索引，便于之后回溯
                readIndex = byteBuf.readerIndex();
                byteBuf.markReaderIndex();

                short magicNumber = byteBuf.readShort();
                if (magicNumber == MAGIC_NUMBER) {
                    break;
                } else {
                    // 如果不是魔数，就结束该通道
                    channelHandlerContext.close();
                    return;
                }
            }

            int contentLength = byteBuf.readInt();
            // 如果剩下的数据不够, 要重置索引
            if (byteBuf.readableBytes() < contentLength) {
                byteBuf.readerIndex(readIndex);
                return;
            }

            byte[] bytes = new byte[contentLength];
            byteBuf.readBytes(bytes);
            RpcProtocol rpcProtocol = new RpcProtocol();
            rpcProtocol.setContent(bytes);
            rpcProtocol.setContentLength(contentLength);
            list.add(rpcProtocol);
        }
    }

}
