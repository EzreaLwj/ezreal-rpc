package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientConfig {

    private String applicationName;

    private String address;

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
}
