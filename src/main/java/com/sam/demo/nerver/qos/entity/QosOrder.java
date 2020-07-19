package com.sam.demo.nerver.qos.entity;

import java.sql.Timestamp;

/**
 * 限流指令
 * <p>
 * <ol>
 * <li>速率rate>0,单位为次数/秒,默认为20
 * <li>峰值peak>=rate*timeWindow/1000.0,默认为200
 * <li>时间窗timeWindow>=1,单位为毫秒,默认为10毫秒
 * </ol>
 * 
 * @author lry
 */
public class QosOrder {

	/**
	 * 流控KEYs,多个服务类型之间使用英文逗号隔开,多个KEY值时,按先后顺序组合为最终的KEY值
	 */
	private String keys;
	/**
	 * 流量控制开关
	 */
	private boolean enable = false;
	/**
	 * 稳态速率
	 */
	private int rate = 20;
	/**
	 * 峰值速率
	 */
	private int peak = 200;
	/**
	 * 时间窗大小,默认
	 */
	private int timeWindow = 10;

	/**
	 * 执行顺序
	 */
	private int order = 0;
	/**
	 * 操作人
	 */
	private String operator;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 修改时间
	 */
	private Timestamp updateTime;

	public QosOrder() {
	}

	public QosOrder(int rate, int peak, int timeWindow) {
		this.rate = rate;
		this.peak = peak;
		this.timeWindow = timeWindow;
	}

	public QosOrder(String keys, boolean enable, int rate, int peak,
			int timeWindow) {
		this.keys = keys;
		this.enable = enable;
		this.rate = rate;
		this.peak = peak;
		this.timeWindow = timeWindow;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getPeak() {
		return peak;
	}

	public void setPeak(int peak) {
		this.peak = peak;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "QosOrder [keys=" + keys + ", enable=" + enable + ", rate="
				+ rate + ", peak=" + peak + ", timeWindow=" + timeWindow
				+ ", order=" + order + ", operator=" + operator
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

}
