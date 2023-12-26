package com.ezreal.rpc.spring.starter.config;

import com.ezreal.rpc.core.client.Client;
import com.ezreal.rpc.core.client.ConnectHandler;
import com.ezreal.rpc.core.client.RpcReference;
import com.ezreal.rpc.core.client.RpcReferenceWrapper;
import com.ezreal.rpc.spring.starter.common.EzrealRpcReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;

/**
 * @author Ezreal
 * @Date 2023/10/27
 */
public class EzrealRpcClientAutoConfigure implements BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

    private static Client client;

    private static RpcReference rpcReference;

    private volatile boolean needInitClient = false;

    private volatile boolean hasInitClientConfig = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(EzrealRpcClientAutoConfigure.class);


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        try {
            // 循环遍历该类的所有字段
            Field[] fields = bean.getClass().getFields();
            for (Field field : fields) {
                // 如果带有EzrealRpcReference注解
                EzrealRpcReference ezrealRpcReference = field.getAnnotation(EzrealRpcReference.class);
                if (ezrealRpcReference != null) {
                    if (!hasInitClientConfig) {
                        client = new Client();
                        client.initClientConfig();
                        rpcReference = client.initClientApplication();
                        hasInitClientConfig = true;
                    }
                    needInitClient = true;

                    field.setAccessible(true);
                    RpcReferenceWrapper rpcReferenceWrapper = new RpcReferenceWrapper<>();
                    rpcReferenceWrapper.setGroup(ezrealRpcReference.group());
                    rpcReferenceWrapper.setServiceToken(ezrealRpcReference.serviceToken());
                    rpcReferenceWrapper.setAimClass(field.getType()); // 获取该字段所属类的具体Class对象
                    rpcReferenceWrapper.setRetry(ezrealRpcReference.retry());
                    rpcReferenceWrapper.setAsync(ezrealRpcReference.async());
                    rpcReferenceWrapper.setUrl(ezrealRpcReference.url());

                    // 获取代理对象，代理对象拦截方法，通过SEND_QUEUE通信
                    Object proxyObject = rpcReference.get(rpcReferenceWrapper);
                    field.set(bean, proxyObject);
                    client.subscribeService(field.getType());

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bean;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (needInitClient && client != null) {
            LOGGER.info(" ================== [{}] started success ================== ", client.getClientConfig().getApplicationName());
            ConnectHandler.setBootstrap(client.getBootstrap());
            client.doConnectServer();
            client.startClientApplication();
        }
    }
}
