package com.ezreal.rpc.core.common.event.data;

import java.util.List;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class URLChangeWrapper {

    private String serviceName;

    private List<String> providerUrl;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(List<String> providerUrl) {
        this.providerUrl = providerUrl;
    }
}
