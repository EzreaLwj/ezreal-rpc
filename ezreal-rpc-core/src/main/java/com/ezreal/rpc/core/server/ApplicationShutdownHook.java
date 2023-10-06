package com.ezreal.rpc.core.server;

import com.ezreal.rpc.core.common.event.ListenerLoader;
import com.ezreal.rpc.core.common.event.ServiceDestroyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ezreal
 * @Date 2023/10/6
 */
public class ApplicationShutdownHook {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationShutdownHook.class);

    public static void registryShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("注销服务...");
                ServiceDestroyEvent serviceDestroyEvent = new ServiceDestroyEvent();
                serviceDestroyEvent.setData("Destroy Event");
                ListenerLoader.send(serviceDestroyEvent);
            }
        }));
    }
}
