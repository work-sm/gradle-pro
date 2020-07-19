package com.sam.demo.nerver.qos.support;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.qos.entity.QosOrder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述：线程安全的令牌桶限流器，时间窗刷新误差为毫秒级。 定义有效的限流器需要满足：
 *
 * @author lry
 */
public class QosBucket {

    protected Logger logger = LoggerFactory.getLogger(QosBucket.class);

    //默认值
    private static final int DEFAULT_RATE = 50;
    private static final int DEFAULT_PEAK = 100;
    private static final int DEFAULT_TIMEWINDOW = 1000;

    /**
     * rate:稳态中，每秒允许的调用次数
     * peak:突发调用峰值的上限，即令牌桶容量
     * timeWindow:令牌桶刷新最小间隔，单位毫秒
     */
    private QosOrder qosOrder;
    private AtomicInteger tokens;// 当前可用令牌数量
    private volatile long lastRefreshTime;// 下一次刷新令牌桶的时间
    private volatile double leftDouble;

    public QosBucket() {
        this(new QosOrder(DEFAULT_RATE, DEFAULT_PEAK, DEFAULT_TIMEWINDOW));
    }

    /**
     * 主要用于初始化创建
     * @param qosOrder
     */
    public QosBucket(QosOrder qosOrder) {
        this.qosOrder = qosOrder;
        double initialToken = qosOrder.getRate() * qosOrder.getTimeWindow() / 1000d;

        //初始的token为零不合理， 改为1
        this.tokens = initialToken >= 1 ? new AtomicInteger((int) initialToken) : new AtomicInteger(1);

        //增加此保存值，是为了double转int时候的不精确；如果不累及这个误差，累计的机会非常大
        this.leftDouble = initialToken - Math.floor(initialToken);

        this.lastRefreshTime = SystemClock.now();
    }

    /**
     * 主要用于持久化存储
     * @param qosOrder
     * @param tokens
     * @param lastRefreshTime
     * @param leftDouble
     */
    public QosBucket(QosOrder qosOrder,AtomicInteger tokens,long lastRefreshTime,double leftDouble) {
        this.qosOrder=qosOrder;
        this.tokens=tokens;
        this.lastRefreshTime=lastRefreshTime;
        this.leftDouble=leftDouble;
    }

    /**
     * 检查令牌前，首先更新令牌数量
     *
     * @return
     */
    public boolean check() {
        long now = SystemClock.now();
        if (now > lastRefreshTime + qosOrder.getTimeWindow()) {// 尝试更新令牌数量
            int currentValue = tokens.get();
            double interval = (now - lastRefreshTime) / 1000d;
            double addedDouble = interval * qosOrder.getRate();
            int added = (int) addedDouble; // 最大值为Integer.MAX_VALUE
            if (added > 0) {
                double addedPlusDouble = leftDouble + (addedDouble - added);
                int addPlus = (int) addedPlusDouble;
                added += addPlus;
                int newValue = currentValue + added;
                newValue = (newValue > currentValue && newValue < qosOrder.getPeak()) ? newValue : qosOrder.getPeak();
                if (tokens.compareAndSet(currentValue, newValue)) {
                    lastRefreshTime = now;// 更新成功后，设置新的刷新时间
                    leftDouble = addedPlusDouble - addPlus;
                    if (logger.isDebugEnabled()) {
                        logger.debug("[" + this.toString() + "] Updated done: [" + currentValue + "] -> [" + newValue + "], refresh time: " + now);
                    }
                }
            }
        }
        int value = tokens.get();// 尝试获得一个令牌
        boolean flag = false; // 是否获得到一个令牌
        while (value > 0 && !flag) {
            flag = tokens.compareAndSet(value, value - 1);
            value = tokens.get();
        }
        if (logger.isDebugEnabled() && !flag) {
            logger.debug("QosBucket: get token failed, tokens[" + tokens.get() + "]");
        }
        return flag;
    }

    /**
     * 限流器有效性验证。限流器的配置必须满足以下条件：
     * <ol>
     * <li>速率rate、峰值peak配置为大于0
     * <li>时间窗timeWindow不小于1
     * <li>峰值不小于速率与时间窗的乘积
     * </ol>
     *
     * @return true/false
     */
    public boolean validate() {
        if (qosOrder.getRate() <= 0 || qosOrder.getPeak() <= 0 || qosOrder.getTimeWindow() < 1) {
            return false;
        }
        if (qosOrder.getPeak() < (qosOrder.getRate() * qosOrder.getTimeWindow() / 1000F)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "QosBucket [tokens=" + tokens + ", rate=" + qosOrder.getRate() + ", peak=" + qosOrder.getPeak() + ", timeWindow=" + qosOrder.getTimeWindow() + "]";
    }
}