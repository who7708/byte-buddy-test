package com.example.bd;

import java.util.Random;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/17
 */
public class BizMethod {
    public String queryUserInfo(String uid, String token) throws InterruptedException {
        Thread.sleep(new Random().nextInt(500));
        return "hello world";
    }

    // public String queryUserInfo() throws InterruptedException {
    //     Thread.sleep(new Random().nextInt(500));
    //     return "hello world";
    // }
}
