package com.ezreal.rpc.core.router;

/**
 * 路由选择器
 * @author Ezreal
 * @Date 2023/10/6
 */
public class Selector {

    /**
     * 服务名 com.ezreal.rpc.core.
     */
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
