package com.ezreal.rpc.core.common.cache;

import com.ezreal.rpc.core.register.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ezreal
 * @Date 2023/10/2
 */
public class ServerServiceCache {

    /**
     * 服务名->暴露对象
     */
    public static final Map<String,Object> PROVIDER_CLASS_MAP = new HashMap<>();

    /**
     * 服务提供者列表
     */
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();

}
