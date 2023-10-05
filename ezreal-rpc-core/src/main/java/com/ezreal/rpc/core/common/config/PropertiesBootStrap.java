package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/5
 */
public class PropertiesBootStrap {

    private final static String ADDRESS = "ezrealRpc.address";
    private final static String APPLICATION_NAME  = "ezrealRpc.applicationName";
    private final static String SERVER_PORT = "ezrealRpc.port";

    public static ClientConfig loadClientConfig() {
        PropertiesLoader.loadConfiguration();

        ClientConfig config = new ClientConfig();
        config.setAddress(PropertiesLoader.getPropertiesStr(ADDRESS));
        config.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        return config;
    }

    public static ServerConfig loadServerConfig() {
        PropertiesLoader.loadConfiguration();;

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(PropertiesLoader.getPropertiesInteger(SERVER_PORT));
        serverConfig.setAddress(PropertiesLoader.getPropertiesStr(ADDRESS));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStr(APPLICATION_NAME));
        return serverConfig;
    }
}
