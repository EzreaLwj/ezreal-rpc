package com.ezreal.rpc.core.common;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class RpcInvocation {

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
}
