package com.example.test.a1;

import com.alibaba.bytekit.utils.Decompiler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/16
 */
public class Test1 {
    @Test
    public void test1() throws IOException, IllegalAccessException, InstantiationException {
        System.out.println("===== test1 =====");
        ByteBuddyAgent.install();
        final DynamicType.Loaded<Log> log = new ByteBuddy()
                .redefine(Log.class)
                .method(ElementMatchers.named("log"))
                .intercept(MethodDelegation.to(Log4j.class))
                .make()
                .load(Thread.currentThread().getContextClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        final byte[] bytes = log.getBytes();
        System.out.println(Decompiler.decompile(bytes));
        final Class<? extends Log> loaded = log.getLoaded();
    }
}
