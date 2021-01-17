package com.example.gw;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2021/01/17
 */
public interface Repository<T> {
    T queryData(int id);
}
