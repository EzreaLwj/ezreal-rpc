package com.ezreal.rpc.core.server;

/**
 * @author Ezreal
 * @Date 2023/10/21
 */
public class ServiceWrapper {

    /**
     * 具体暴露的对象
     */
    private Object serviceObj;

    /**
     * 默认分组
     */
    private String group = "default";

    /**
     * 默认token
     */
    private String serviceToken = "";

    /**
     * 限流策略
     */
    private Integer limit = -1;


    public ServiceWrapper(Object serviceObj) {
        this.serviceObj = serviceObj;
    }

    public ServiceWrapper(Object serviceObj, String group) {
        this.serviceObj = serviceObj;
        this.group = group;
    }

    public Object getServiceObj() {
        return serviceObj;
    }

    public void setServiceObj(Object serviceObj) {
        this.serviceObj = serviceObj;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceToken() {
        return serviceToken;
    }

    public void setServiceToken(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
