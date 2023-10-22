package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientConfig {

    private String applicationName;

    private String address;

    private String routerStrategy;

    private String clientSerialize;

    /**
     * 注册中心类型
     */
    private String registerType;

    public ClientConfig() {
    }

    public ClientConfig(String applicationName, String address) {
        this.applicationName = applicationName;
        this.address = address;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRouterStrategy() {
        return routerStrategy;
    }

    public void setRouterStrategy(String routerStrategy) {
        this.routerStrategy = routerStrategy;
    }

    public String getClientSerialize() {
        return clientSerialize;
    }

    public void setClientSerialize(String clientSerialize) {
        this.clientSerialize = clientSerialize;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }
}
