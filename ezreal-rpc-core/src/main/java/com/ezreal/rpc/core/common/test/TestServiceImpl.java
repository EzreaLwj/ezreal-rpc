package com.ezreal.rpc.core.common.test;

/**
 * @author Ezreal
 * @Date 2023/10/3
 */
public class TestServiceImpl implements TestService {

    @Override
    public String hello() {
        return "Hello world";
    }

    @Override
    public String sayName(String name, Integer count) {
        return "my name is " + name + " count is " + count;
    }
}
