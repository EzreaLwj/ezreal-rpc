package com.ezreal.rpc.core.exception;

import com.ezreal.rpc.core.common.RpcInvocation;

/**
 * @author Ezreal
 * @Date 2023/10/25
 */
public class MaxServiceLimitRequestException extends IRpcException{


    public MaxServiceLimitRequestException(RpcInvocation rpcInvocation) {
        super(rpcInvocation);
    }
}
