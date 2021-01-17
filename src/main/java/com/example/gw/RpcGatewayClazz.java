package com.example.gw;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcGatewayClazz {
    String clazzDesc() default "";

    String alias() default "";

    int timeOut() default 300;
}
