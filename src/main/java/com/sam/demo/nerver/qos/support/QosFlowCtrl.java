package com.sam.demo.nerver.qos.support;

import com.sam.demo.nerver.qos.entity.QosOrder;
import com.sam.demo.nerver.qos.storage.IQosStorage;
import com.sam.demo.nerver.qos.storage.support.MemoryMapQosStorage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * 基于QoS的速率控制规则
 * <p>
 * 解释：根据配置决定RPC调用速率
 *
 * @author lry
 */
public class QosFlowCtrl {

    protected Logger logger = LoggerFactory.getLogger(QosFlowCtrl.class);

    private QosOrder qosOrder;
    private QosBucket defaultQoSBucket;
    /**
     * 持久化对象
     */
    private IQosStorage storage;

    public IQosStorage getStorage(){
        return storage;
    }

    /**
     * 流量控制
     *
     * @param qosOrder 流量控制开关
     */
    public QosFlowCtrl(QosOrder qosOrder) {
        this.qosOrder = qosOrder;
        //内存存储
        this.storage = new MemoryMapQosStorage();

        //校验
        if (!this.qosOrder.isEnable()) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Init QosOrder...");
        }

        QosBucket qosBucket = this.createQoSBucket();
        if (!qosBucket.validate()) {
            this.qosOrder.setEnable(false);
            if (logger.isInfoEnabled()) {
                logger.info("QosOrder config validate fail. -> " + this.defaultQoSBucket);
            }
            return;
        }
        this.defaultQoSBucket = qosBucket;
    }

    /**
     * 拉起流量控制校验
     *
     * @param qosKey
     * @return
     */
    public boolean callCheck(String qosKey) {
        try{
            if (!this.qosOrder.isEnable()) {
                return true;
            }
            //校验
            if (qosKey == null) {
                return true;
            }

            //获取
            QosBucket qos = storage.get(qosKey);
            if (qos == null) {
                this.storage.put(qosKey, this.createQoSBucket());
                qos = this.storage.get(qosKey);
            }

            //校验
            boolean flag=qos.check();

            //更新
            //this.storage.put(qosKey, this.createQoSBucket());

            return flag;
        }catch (Throwable t){
            logger.error("callCheck is fail, error "+t.getMessage(),t);
            return true;
        }
    }

    /**
     * 创建
     *
     * @return
     */
    protected QosBucket createQoSBucket() {
        QosBucket qosBucket = new QosBucket(this.qosOrder);
        if (logger.isInfoEnabled()) {
            logger.info("Create " + qosBucket);
        }
        return qosBucket;
    }

}