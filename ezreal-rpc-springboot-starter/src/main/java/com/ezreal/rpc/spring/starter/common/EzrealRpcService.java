package com.ezreal.rpc.spring.starter.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Ezreal
 * @Date 2023/10/27
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EzrealRpcService {

    int limit() default 0;

    String group() default "default";

    String serviceToken() default "";

}
