package com.sam.demo.neural.config;

import com.sam.demo.neural.OriginalCall;
import com.sam.demo.neural.common.URL;
import com.sam.demo.neural.config.store.StorePool;
import com.sam.demo.neural.limiter.Limiter;
import com.sam.demo.neural.limiter.LimiterConfig;

public class RedisStoreTest {

    public static void main(String[] args) throws Throwable {
        String key1 = "micro:neural:test";

        Limiter limiter = new Limiter();
        LimiterConfig limiterConfig1 = new LimiterConfig();
        limiterConfig1.setResource("test");
        limiterConfig1.setModel("cluster");
        limiterConfig1.setConcurrentEnable(true);
        limiterConfig1.setRateEnable(false);
        limiterConfig1.setRequestEnable(false);
        limiter.addConfig(limiterConfig1);

        URL url = URL.valueOf("redis://127.0.0.1:6379");
        StorePool.INSTANCE.initialize(url);

        Object result = limiter.originalCall(key1, new OriginalCall() {
            @Override
            public Object call() throws Throwable {
                System.out.println("Input call");
                return "return Input call";
            }
        });
        System.out.println(result);
    }

}
