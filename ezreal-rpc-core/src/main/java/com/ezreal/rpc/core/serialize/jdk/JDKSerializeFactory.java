package com.ezreal.rpc.core.serialize.jdk;

import com.ezreal.rpc.core.serialize.SerializeFactory;

import java.io.*;

/**
 * @author Ezreal
 * @Date 2023/10/20
 */
public class JDKSerializeFactory implements SerializeFactory {

    @Override
    public <T> byte[] serialize(T t) {

        byte[] data = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(byteArrayOutputStream);
            output.writeObject(t);
            output.flush();
            output.close();

            data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        ByteArrayInputStream is = new ByteArrayInputStream(data);
        try {

            ObjectInputStream input = new ObjectInputStream(is);
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
