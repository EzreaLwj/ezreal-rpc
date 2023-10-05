package com.ezreal.rpc.core.common.event;

/**
 * @author Ezreal
 * @Date 2023/10/4
 */
public class UpdateServiceEvent implements RpcEvent{

    private Object data;

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

}
