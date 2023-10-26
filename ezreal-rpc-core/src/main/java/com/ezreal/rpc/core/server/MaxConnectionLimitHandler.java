package com.ezreal.rpc.core.server;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Ezreal
 * @Date 2023/10/25
 */
public class MaxConnectionLimitHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaxConnectionLimitHandler.class);
    private int maxConnectionNum;

    private AtomicInteger numConnection = new AtomicInteger(0);

    private final Set<Channel> childChannel = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final AtomicBoolean loggingScheduled = new AtomicBoolean(false);

    private final LongAdder numDroppedConnections = new LongAdder();

    public MaxConnectionLimitHandler(int maxConnectionNum) {
        this.maxConnectionNum = maxConnectionNum;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = (Channel) msg;

        // 当前连接的线程数
        int conn = numConnection.getAndIncrement();
        if (conn >= 0 && conn < maxConnectionNum) {
            childChannel.add(channel);
            channel.closeFuture().addListener((future -> {
                childChannel.remove(channel);
                numConnection.decrementAndGet();
            }));
            super.channelRead(ctx, msg);
        } else {
            numConnection.decrementAndGet();

            // 避免产生大量的 time_wait 连接
            channel.config().setOption(ChannelOption.SO_LINGER, 0);
            channel.unsafe().closeForcibly();

            //这里加入一道cas可以减少一些并发请求的压力,定期地执行一些日志打印
            if (loggingScheduled.compareAndSet(false, true)) {
                ctx.executor().schedule(this::writeNumDroppedConnectionLog,1, TimeUnit.SECONDS);
            }
        }

    }

    /**
     * 记录连接失败的日志
     */
    private void writeNumDroppedConnectionLog() {
        loggingScheduled.set(false);
        final long dropped = numDroppedConnections.sumThenReset();
        if(dropped>0){
            LOGGER.error("Dropped {} connection(s) to protect server,maxConnection is {}",dropped,maxConnectionNum);
        }
    }

}
