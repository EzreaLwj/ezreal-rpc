package com.ezreal.rpc.core.serialize.fastjson;

import com.alibaba.fastjson.JSON;
import com.ezreal.rpc.core.serialize.SerializeFactory;

/**
 * @author Ezreal
 * @Date 2023/10/20
 */
public class FastJsonSerializeFactory implements SerializeFactory {

    @Override
    public <T> byte[] serialize(T t) {
        String jsonString = JSON.toJSONString(t);
        return jsonString.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        String jsonStr = new String(data);
        return JSON.parseObject(jsonStr, clazz);
    }

}
