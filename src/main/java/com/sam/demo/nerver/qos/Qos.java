package com.sam.demo.nerver.qos;

import com.sam.demo.nerver.qos.entity.QosOrder;
import com.sam.demo.nerver.qos.support.QosFlowCtrl;
import com.sam.demo.nerver.qos.type.QosLevel;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流控控制工厂
 *
 * @author lry
 */
public enum Qos {

    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(Qos.class);

    /**
     * 所有规则
     */
    private ConcurrentHashMap<String, QosFlowCtrl> qosFlowCtrlMap;

    public static final String SEQ = ",";

    /**
     * 初始化控制规则
     *
     * @param orders
     */
    public void init(List<QosOrder> orders) {
        if (qosFlowCtrlMap == null) {
            qosFlowCtrlMap = new ConcurrentHashMap<String, QosFlowCtrl>();
        } else {
            qosFlowCtrlMap.clear();
        }

        for (QosOrder order : orders) {
            String[] keyArray = order.getKeys().split(SEQ);
            if (keyArray == null || keyArray.length < 1) {
                continue;
            }
            for (String key : keyArray) {
                try {
                    QosLevel.valueOf(key);
                } catch (Throwable t) {
                    throw new RuntimeException("Keys is unknown, keys=" + key);
                }
            }
            qosFlowCtrlMap.put(order.getKeys(), new QosFlowCtrl(order));
        }
    }

    /**
     * 流量控制校验
     * <p><p>
     * 1.按照规则将所有的KEY值拼接好<p>
     * 2.按照KEY进行依次校验<p>
     *
     * @param map
     * @return
     */
    public boolean checks(Map<QosLevel, String> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        //校验必须含有所有的QosLevel
        for (QosLevel qosLevel : QosLevel.values()) {
            if (!map.containsKey(qosLevel)) {
                return true;
            }
        }

        //加载所有规则
        for (Map.Entry<String, QosFlowCtrl> entry : qosFlowCtrlMap.entrySet()) {
            String[] keyArray = entry.getKey().split(SEQ);
            if (keyArray == null || keyArray.length < 1) {
                continue;
            }

            //拼接规则KEYS
            String keys = "";
            for (int i = 0; i < keyArray.length; i++) {
                try {
                    keys += map.get(QosLevel.convert(keyArray[i]));
                    if (i < keyArray.length - 1) {
                        keys += SEQ;
                    }
                } catch (Throwable t) {
                    logger.error("The [" + entry.getKey() + "] convert QosLevel is fail, error " + t.getMessage(), t);
                    break;//跳过本次异常的流量控制资源
                }
            }

            QosFlowCtrl ctrl = entry.getValue();
            boolean flag = ctrl.callCheck(keys);
            qosFlowCtrlMap.put(entry.getKey(), ctrl);
            //校验当前KEY值的流控情况,失败则退出不在继续校验
            if (!flag) {
                return false;
            }
        }

        return true;
    }

}
