package com.kirussell.garson;

/**
 * Created by russellkim on 15/02/16.
 */
public interface Action<T> {
    void run(T value);
}
