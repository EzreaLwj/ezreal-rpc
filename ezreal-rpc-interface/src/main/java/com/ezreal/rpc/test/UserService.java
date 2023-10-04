package com.ezreal.rpc.test;

/**
 * @author Ezreal
 * @Date 2023/10/5
 */
public interface UserService {

    String getUserInfo(String name, Integer number);

    String talk(String name);
}
