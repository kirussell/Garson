package com.kirussell.garson;

/**
 * Created by russellkim on 15/02/16.
 * Just like Runnable, but data can be passed when run is called
 */
public interface Action<T> {
    void run(T value);
}
