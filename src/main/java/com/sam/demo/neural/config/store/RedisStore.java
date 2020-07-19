package com.sam.demo.neural.config.store;

import lombok.extern.slf4j.Slf4j;
import com.sam.demo.neural.common.URL;

import java.util.*;

@Slf4j
public enum RedisStore {

    //===

    INSTANCE;


    public synchronized void initialize(URL url) {

    }

    public void batchIncrementBy(String key, Map<String, Object> data, long expire) {

    }

    public void putAllMap(String space, Map<String, String> data) {
    }

    public List<Object> eval(String script, Long timeout, List<Object> keys) {
        return null;
    }

    public Map<String, String> getMap(String name) {
        return null;
    }

    public void publish(String channel, String data) {
    }

    public void subscribe(String pattern, IStoreListener listener) {
    }

    public void unsubscribe(String pattern) {
    }

    public void destroy() {
    }

}
