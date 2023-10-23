package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/5
 */
public class PropertiesBootStrap {

    private final static String ADDRESS = "ezrealRpc.address";
    private final static String APPLICATION_NAME  = "ezrealRpc.applicationName";
    private final static String SERVER_PORT = "ezrealRpc.port";
    private final static String ROUTER_STRATEGY = "ezrealRpc.routerStrategy";

    private final static String SERVER_SERIALIZE = "ezrealRpc.serverSerialize";

    private final static String CLIENT_SERIALIZE = "ezrealRpc.clientSerialize";

    public static final String REGISTER_TYPE = "ezrealRpc.registerType";

    public static final String SERVER_BIZ_THREAD_NUMS = "ezrealRpc.server.biz.thread.nums";

    public static final String SERVER_QUEUE_SIZE = "ezrealRpc.server.queue.size";

    public static ClientConfig loadClientConfig() {
        PropertiesLoader.loadConfiguration();

        ClientConfig config = new ClientConfig();
        config.setAddress(PropertiesLoader.getPropertiesStr(ADDRESS));
        config.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        config.setRouterStrategy(PropertiesLoader.getPropertiesStr(ROUTER_STRATEGY));
        config.setClientSerialize(PropertiesLoader.getPropertiesStr(CLIENT_SERIALIZE));
        config.setRegisterType(PropertiesLoader.getPropertiesStr(REGISTER_TYPE));

        return config;
    }

    public static ServerConfig loadServerConfig() {
        PropertiesLoader.loadConfiguration();;

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setAddress(PropertiesLoader.getPropertiesStr(ADDRESS));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStr(SERVER_SERIALIZE));
        serverConfig.setServerBizThreadNums(PropertiesLoader.getPropertiesInteger(SERVER_BIZ_THREAD_NUMS));
        serverConfig.setServerQueueSize(PropertiesLoader.getPropertiesInteger(SERVER_QUEUE_SIZE));
        return serverConfig;
    }
}
