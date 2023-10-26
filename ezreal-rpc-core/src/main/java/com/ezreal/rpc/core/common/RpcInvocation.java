package com.ezreal.rpc.core.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class RpcInvocation implements Serializable {

    private static final long serialVersionUID = 8771637069558685122L;

    /**
     * 类的全路径
     */
    private String serviceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] args;

    /**
     * uuid 匹配请求和响应
     */
    private String uuid;

    /**
     * 返回值
     */
    private Object response;

    private Throwable e;

    private int retry;

    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
