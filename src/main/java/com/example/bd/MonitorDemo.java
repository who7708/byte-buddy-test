package com.example.bd;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/17
 */
public class MonitorDemo {
    @RuntimeType
    public static Object intercept(
            // 第一个入参
            @Argument(0) Object arg0
            // 绑定所有的参数的数组
            , @AllArguments Object[] args
            // 当前被拦截的,动态生成的对象
            , @This Object dynamicObj
            // @this 的父类对象
            , @Super Object superObj
            , @Origin Method method
            // , @Origin Constructor<?> constructor
            // , @Origin MethodHandle methodHandle
            // , @Origin MethodType methodType
            // , @Origin int access
            , @SuperCall Callable<?> callable) throws Exception {
        long start = System.currentTimeMillis();
        Object obj = null;
        try {
            obj = callable.call();
            return obj;
        } finally {
            System.out.println("方法名称: " + method.getName());
            System.out.println("入参个数: " + method.getParameterCount());
            System.out.println("入参类型: " + Arrays.stream(method.getParameterTypes()).map(Class::getTypeName).collect(Collectors.toList()));
            System.out.println("入参内容: " + Arrays.asList(args));
            System.out.println("出参类型: " + method.getReturnType().getName());
            System.out.println("出参结果: " + obj);
            System.out.println("方法耗时: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
