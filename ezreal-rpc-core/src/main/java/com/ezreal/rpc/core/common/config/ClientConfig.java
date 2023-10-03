package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class ClientConfig {

    private int port;

    private String host;

    public ClientConfig() {
    }

    public ClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
