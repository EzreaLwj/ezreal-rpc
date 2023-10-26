package com.ezreal.rpc.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author Ezreal
 * @Date 2023/10/26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {

    String value() default "";
}
