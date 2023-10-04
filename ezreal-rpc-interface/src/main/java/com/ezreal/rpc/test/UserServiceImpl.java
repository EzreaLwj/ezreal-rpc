package com.ezreal.rpc.test;

/**
 * @author Ezreal
 * @Date 2023/10/5
 */
public class UserServiceImpl implements UserService{

    @Override
    public String getUserInfo(String name, Integer number) {
        return "my name is " +name +" number is " + number;
    }

    @Override
    public String talk(String name) {
        return "hello world";
    }
}
