package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class ServerConfig {

    private String applicationName;

    private String address;

    private Integer port;

    public ServerConfig() {
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
