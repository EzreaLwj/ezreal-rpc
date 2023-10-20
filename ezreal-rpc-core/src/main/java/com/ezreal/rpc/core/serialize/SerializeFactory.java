package com.ezreal.rpc.core.serialize;

/**
 * @author Ezreal
 * @Date 2023/10/20
 */
public interface SerializeFactory {

    /**
     * 序列化
     *
     * @param t 序列化对象
     * @return 字节数组
     */
    <T> byte[] serialize(T t);


    /**
     * 反序列化
     *
     * @param data 对象的字节数组
     * @param t    Class对象
     * @return 结果
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
