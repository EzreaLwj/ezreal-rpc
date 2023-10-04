package com.ezreal.rpc.core.register.zookeeper;

/**
 * 服务提供者节点信息
 *
 * @author Ezreal
 * @Date 2023/10/4
 */
public class ProviderNodeInfo {

    private String serviceName;

    private String address;

    public ProviderNodeInfo() {
    }

    public ProviderNodeInfo(String serviceName, String address) {
        this.serviceName = serviceName;
        this.address = address;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ProviderNodeInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
