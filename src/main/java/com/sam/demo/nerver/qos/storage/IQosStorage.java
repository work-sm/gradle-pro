package com.sam.demo.nerver.qos.storage;

import com.sam.demo.nerver.qos.support.QosBucket;

/**
 * 流量控制持久化
 *
 * @author lry
 */
public interface IQosStorage {

    /**
     * 查看当前仓库的容量大小
     * @return
     */
    int size();

    /**
     * 获取存储桶(没有则返回null)
     *
     * @param qosKey
     * @return
     */
    QosBucket get(String qosKey);

    /**
     * 创建或更新一个存储桶
     *
     * @param key
     * @param qosBucket
     */
    void put(String key, QosBucket qosBucket);

}
