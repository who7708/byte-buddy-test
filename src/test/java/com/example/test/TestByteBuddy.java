package com.example.test;

import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.Decompiler;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.example.bd.BizMethod;
import com.example.bd.MonitorDemo;
import com.example.gw.Repository;
import com.example.gw.RpcGatewayClazz;
import com.example.gw.RpcGatewayMethod;
import com.example.gw.UserRepositoryInterceptor;
import com.example.model.Bar;
import com.example.model.Foo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/16
 */
public class TestByteBuddy {

    @Test
    public void test7() throws IOException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("===== test7 =====");
        // 生成含有注解的泛型实现类
        final DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .subclass(TypeDescription.Generic.Builder.parameterizedType(Repository.class, String.class).build())
                .name(Repository.class.getPackage().getName().concat(".").concat("UserRepository"))
                .method(ElementMatchers.named("queryData"))
                .intercept(MethodDelegation.to(UserRepositoryInterceptor.class))
                .annotateMethod(AnnotationDescription.Builder
                        .ofType(RpcGatewayMethod.class)
                        .define("methodName", "queryData")
                        .define("methodDesc", "查询数据").build())
                .annotateType(AnnotationDescription.Builder
                        .ofType(RpcGatewayClazz.class)
                        .define("alias", "dataApi")
                        .define("clazzDesc", "查询数据信息")
                        .define("timeOut", 350)
                        .build())
                .make();
        dynamicType.saveIn(new File(TestByteBuddy.class.getResource("/").getPath()));

        // 从目标文件夹下加载类信息
        final Class<Repository<String>> userRepository = (Class<Repository<String>>) Class.forName(Repository.class.getPackage().getName().concat(".").concat("UserRepository"));
        // 获取类注解
        RpcGatewayClazz rpcGatewayClazz = userRepository.getAnnotation(RpcGatewayClazz.class);
        System.out.println("RpcGatewayClazz.clazzDesc：" + rpcGatewayClazz.clazzDesc());
        System.out.println("RpcGatewayClazz.alias：" + rpcGatewayClazz.alias());
        System.out.println("RpcGatewayClazz.timeOut：" + rpcGatewayClazz.timeOut());

        // 获取方法注解
        RpcGatewayMethod rpcGatewayMethod = userRepository.getMethod("queryData", int.class).getAnnotation(RpcGatewayMethod.class);
        System.out.println("RpcGatewayMethod.methodName：" + rpcGatewayMethod.methodName());
        System.out.println("RpcGatewayMethod.methodDesc：" + rpcGatewayMethod.methodDesc());

        final Repository<String> stringRepository = userRepository.newInstance();
        final String queryData = stringRepository.queryData(1001);
        System.out.println(queryData);
    }

    @Test
    public void test6() throws IOException, InterruptedException, IllegalAccessException, InstantiationException {
        System.out.println("===== test6 =====");
        final DynamicType.Unloaded<BizMethod> dynamicType = new ByteBuddy()
                .subclass(BizMethod.class)
                .method(ElementMatchers.named("queryUserInfo"))
                .intercept(MethodDelegation.to(MonitorDemo.class))
                .make();
        System.out.println(Decompiler.decompile(dynamicType.getBytes()));
        final BizMethod bizMethod = dynamicType.load(TestByteBuddy.class.getClassLoader())
                .getLoaded().newInstance();
        final String a = bizMethod.queryUserInfo("10001", "Adbsadf");
        System.out.println(a);
        // final String b = bizMethod.queryUserInfo();
        // System.out.println(b);
    }

    @Test
    public void test1() throws Exception {
        System.out.println("===== test1 =====");
        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World"))
                .make()
                .load(TestByteBuddy.class.getClassLoader())
                .getLoaded();

        Object instance = dynamicType.newInstance();
        String toString = instance.toString();
        System.out.println(toString);
        System.out.println(instance.getClass().getCanonicalName());
    }

    @Test
    public void test2() throws IOException {
        System.out.println("===== test2 =====");
        final DynamicType.Unloaded<Object> make = new ByteBuddy()
                // .with(new NamingStrategy.PrefixingRandom("test"))
                .subclass(Object.class)
                // .name("example.Type")
                // .suffix("example.Foo")
                .make();
        decompile(make.getBytes());
        final Class<?> loaded = make.load(TestByteBuddy.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        final ClassNode classNode = AsmUtils.loadClass(loaded);
        // final ClassReader classReader = new ClassReader(loaded.getName());
        // final ClassNode classNode = new ClassNode(Opcodes.ASM8);
        // classReader.accept(classNode, ClassReader.SKIP_DEBUG);
        // final byte[] bytes = AsmUtils.toBytes(classNode);
        //
        // final String decompile1 = Decompiler.decompile(make.getBytes());
        // System.out.println(decompile1);
    }

    @Test
    public void test3() {
        System.out.println("===== test3 =====");
        ByteBuddyAgent.install();

        Foo foo = new Foo();
        final DynamicType.Unloaded<Bar> make = new ByteBuddy()
                .redefine(Bar.class)
                .name(Foo.class.getName())
                .make();
        decompile(make.getBytes());

        make.load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        System.out.println(foo.foo());
    }

    private void decompile(byte[] bytes) {
        try {
            final String decompile = Decompiler.decompile(bytes);
            System.out.println(decompile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test4() {
        System.out.println("===== test4 =====");
        final String url = "https://services.gradle.org/distributions/gradle-6.5-all.zip";
        final String hash = getHash(url);
        System.out.println(hash);
    }

    private String getHash(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = string.getBytes();
            messageDigest.update(bytes);
            return new BigInteger(1, messageDigest.digest()).toString(36);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash input string.", e);
        }
    }

    @Test
    public void test5() {
        System.out.println("===== test5 =====");
        System.out.println(Long.MAX_VALUE);
    }
}
