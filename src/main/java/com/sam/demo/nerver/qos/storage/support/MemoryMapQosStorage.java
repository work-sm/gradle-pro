package com.sam.demo.nerver.qos.storage.support;

import com.sam.demo.nerver.qos.storage.IQosStorage;
import com.sam.demo.nerver.qos.support.QosBucket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于MAP的内存存储方案
 *
 * @author lry
 */
public class MemoryMapQosStorage implements IQosStorage {

    private ConcurrentMap<String, QosBucket> qosBucketMap;

    public MemoryMapQosStorage() {
        this.qosBucketMap = new ConcurrentHashMap<String, QosBucket>();
    }

    /**
     * 当前容量
     * @return
     */
    public int size() {
        return qosBucketMap.size();
    }

    /**
     * 获取资源
     * @param qosKey
     * @return
     */
    public QosBucket get(String qosKey) {
        return qosBucketMap.get(qosKey);
    }

    /**
     * 创建或更新资源
     * @param key
     * @param qosBucket
     */
    public void put(String key, QosBucket qosBucket) {
        qosBucketMap.put(key, qosBucket);
    }

}
