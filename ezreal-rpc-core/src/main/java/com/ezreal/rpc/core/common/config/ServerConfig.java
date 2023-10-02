package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class ServerConfig {

    private int port;

    public ServerConfig() {
    }

    public ServerConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
