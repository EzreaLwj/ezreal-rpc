package com.ezreal.rpc.spring.starter.config;

import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.server.ApplicationShutdownHook;
import com.ezreal.rpc.core.server.Server;
import com.ezreal.rpc.core.server.ServiceWrapper;
import com.ezreal.rpc.spring.starter.common.EzrealRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Set;

/**
 * @author Ezreal
 * @Date 2023/10/27
 */
public class EzrealRpcServerAutoConfigure implements InitializingBean , ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(EzrealRpcServerAutoConfigure.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Server server = null;
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(EzrealRpcService.class);
        if (beanMap.isEmpty()) {
            return;
        }
        server = new Server();

        long start = System.currentTimeMillis();
        // 打印banner
        printBanner();
        server.initServerConfig();;
        ListenerLoader listenerLoader = new ListenerLoader();
        listenerLoader.init();

        // 将所有Bean暴露给注册中心
        Set<String> beanNames = beanMap.keySet();
        for (String beanName : beanNames) {

            Object bean = beanMap.get(beanName);
            EzrealRpcService rpcService = bean.getClass().getAnnotation(EzrealRpcService.class);
            ServiceWrapper serviceWrapper = new ServiceWrapper(bean, rpcService.group());
            serviceWrapper.setServiceToken(rpcService.serviceToken());
            serviceWrapper.setLimit(rpcService.limit());
            // 暴露服务
            server.exportService(serviceWrapper);

            LOGGER.info(">>>>>>>>>>>>>>> [irpc] {} export success! >>>>>>>>>>>>>>> ",beanName);
        }

        ApplicationShutdownHook.registryShutDownHook();
        server.setOnApplication();
        long end = System.currentTimeMillis();
        LOGGER.info(" ================== [{}] started success in {}s ================== ",server.getServerConfig().getApplicationName(),((double)end-(double)start)/1000);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void printBanner(){
        System.out.println();
        System.out.println("==============================================");
        System.out.println("|||-------- EzrealRpc Starting Now! --------|||");
        System.out.println("==============================================");
        System.out.println("源代码地址: https://gitee.com/IdeaHome_admin/irpc-framework");
        System.out.println("version: 1.0.0");
        System.out.println();
    }
}
