package com.ezreal.rpc.core.common.config;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class ServerConfig {

    private String applicationName;

    private String address;

    private Integer port;

    private String serverSerialize;

    /**
     * 服务端业务线程数目
     */
    private Integer serverBizThreadNums;

    /**
     * 服务端接收队列的大小
     */
    private Integer serverQueueSize;

    /**
     * 限制服务端最大所能接受的数据包体积
     */
    private Integer maxServerRequestData;

    /**
     * 服务端最大连接数
     */
    private Integer maxConnections;

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

    public String getServerSerialize() {
        return serverSerialize;
    }

    public void setServerSerialize(String serverSerialize) {
        this.serverSerialize = serverSerialize;
    }

    public Integer getServerBizThreadNums() {
        return serverBizThreadNums;
    }

    public void setServerBizThreadNums(Integer serverBizThreadNums) {
        this.serverBizThreadNums = serverBizThreadNums;
    }

    public Integer getServerQueueSize() {
        return serverQueueSize;
    }

    public void setServerQueueSize(Integer serverQueueSize) {
        this.serverQueueSize = serverQueueSize;
    }

    public Integer getMaxServerRequestData() {
        return maxServerRequestData;
    }

    public void setMaxServerRequestData(Integer maxServerRequestData) {
        this.maxServerRequestData = maxServerRequestData;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }
}
