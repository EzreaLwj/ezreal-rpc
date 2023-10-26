package com.ezreal.rpc.core.exception;

import com.ezreal.rpc.core.common.RpcInvocation;

/**
 * @author Ezreal
 * @Date 2023/10/25
 */
public class IRpcException extends RuntimeException{

    private RpcInvocation rpcInvocation;

    public RpcInvocation getRpcInvocation() {
        return rpcInvocation;
    }

    public void setRpcInvocation(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }

    public IRpcException(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }
}
