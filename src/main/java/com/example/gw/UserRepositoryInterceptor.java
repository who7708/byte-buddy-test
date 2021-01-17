package com.example.gw;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;

import java.lang.reflect.Method;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/17
 */
public class UserRepositoryInterceptor {
    public static String intercept(@Origin Method method, @AllArguments Object[] args) {
        return "查询文章数据: https://bugstack.cn/?id=" + args[0];
    }
}
