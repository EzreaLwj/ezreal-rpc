package com.ezreal.rpc.core.dispatcher;

import com.ezreal.rpc.core.common.RpcInvocation;
import com.ezreal.rpc.core.common.RpcProtocol;
import com.ezreal.rpc.core.exception.IRpcException;
import com.ezreal.rpc.core.server.ServerChannelReadData;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static com.ezreal.rpc.core.common.cache.ServerServiceCache.*;

/**
 * 服务端请求分发器
 *
 * @author Ezreal
 * @Date 2023/10/23
 */
public class ServerChannelDispatcher {

    private BlockingQueue<ServerChannelReadData> blockingQueue;

    private ExecutorService executorService;

    public void init(int queueSize, int bizThreadNums) {
        blockingQueue = new ArrayBlockingQueue<>(512);
        executorService = new ThreadPoolExecutor(bizThreadNums, bizThreadNums, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(512), new ThreadPoolExecutor.AbortPolicy());
    }

    public void add(ServerChannelReadData serverChannelReadData) {
        blockingQueue.add(serverChannelReadData);
    }

    public void startServerJobCoreHandler() {
        Thread thread = new Thread(new ServerJobCoreHandler());
        thread.start();
    }

    class ServerJobCoreHandler implements Runnable{

        @Override
        public void run() {

           while (true) {
               try {
                   ServerChannelReadData serverChannelReadData = blockingQueue.take();
                   executorService.submit(new Runnable() {
                       @Override
                       public void run() {

                           try {
                               ChannelHandlerContext ctx = serverChannelReadData.getChannelHandlerContext();
                               // 序列化
                               RpcProtocol rpcProtocol = serverChannelReadData.getProtocol();
                               RpcInvocation rpcInvocation = SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(), RpcInvocation.class);

                               // 执行前过滤器链
                               try {
                                   SERVER_BEFORE_FILTER_CHAIN.doFilter(rpcInvocation);
                               } catch (Exception e) {
                                   if (e instanceof IRpcException) {
                                       IRpcException rpcException = (IRpcException) e;
                                       RpcInvocation reqParam = rpcException.getRpcInvocation();
                                       rpcInvocation.setE(rpcException);
                                       byte[] body = SERVER_SERIALIZE_FACTORY.serialize(reqParam);
                                       RpcProtocol respRpcProtocol = new RpcProtocol(body);
                                       serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
                                       return;
                                   }
                               }

                               String serviceName = rpcInvocation.getServiceName();
                               Object beanService = PROVIDER_CLASS_MAP.get(serviceName);

                               Method[] methods = beanService.getClass().getMethods();
                               Object result = null;
                               for (Method method : methods) {
                                   if (method.getName().equals(rpcInvocation.getMethodName())) {
                                       if (method.getReturnType().equals(Void.class)) {
                                           try {
                                               method.invoke(beanService, rpcInvocation.getArgs());
                                           } catch (Exception e) {
                                               rpcInvocation.setE(e);
                                           }
                                       } else {
                                           try {
                                               result = method.invoke(beanService, rpcInvocation.getArgs());
                                           } catch (Exception e) {
                                               rpcInvocation.setE(e);
                                           }
                                       }
                                   }
                               }

                               // 执行后过滤器
                               SERVER_AFTER_FILTER_CHAIN.doFilter(rpcInvocation);
                               rpcInvocation.setResponse(result);
                               RpcProtocol responseRpcProtocol = new RpcProtocol(SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation));
                               ctx.writeAndFlush(responseRpcProtocol);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                   });
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
           }
        }
    }
}
