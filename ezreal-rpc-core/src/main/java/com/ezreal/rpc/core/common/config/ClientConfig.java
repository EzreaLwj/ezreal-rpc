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

    /**
     * 客户端发数据的超时时间
     */
    private Integer timeOut;

    /**
     * 客户端最大响应数据体积
     */
    private Integer maxServerRespDataSize;

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

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public Integer getMaxServerRespDataSize() {
        return maxServerRespDataSize;
    }

    public void setMaxServerRespDataSize(Integer maxServerRespDataSize) {
        this.maxServerRespDataSize = maxServerRespDataSize;
    }
}
