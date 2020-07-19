package com.sam.demo.nerver.neural.entity;

import com.sam.demo.nerver.degrade.type.DegradeType;
import com.sam.demo.nerver.degrade.type.StrategyType;
import com.sam.demo.nerver.neural.support.NeuralSupport;
import com.netflix.hystrix.HystrixCommand.Setter;

import java.util.UUID;

/**
 * 模块配置
 * 
 * @author lry
 */
public class NeuralConf {

	//$NON-NLS-核心配置$
	/**
	 * 微服务神经元消息ID
	 */
	private String neuralId=UUID.randomUUID().toString();
	/**
	 * 容错配置
	 */
	private Setter setter=NeuralSupport.buildByFaultTolerance();
	/**
	 * 放通率:用于控制后端服务放通的概率,默认1.0表示100%放通,即不做限制
	 */
	private double passRate=1.0;
	/**
	 * 最大重试次数
	 */
	private int maxRetryNum=0;
	/**
	 * 重试休眠周期,默认不休眠
	 */
	private long retryCycle=0;
	/**
	 * mock服务开关
	 */
	private boolean mockEnable=false;
	/**
	 * 失败通知开关
	 */
	private boolean failNotifyEnable=true;
	/**
	 * 异步回调开关
	 */
	private boolean callbackEnable=false;
	
	
	//$NON-NLS-服务降级配置$
	/**
	 * 服务降级开关
	 */
	private boolean degradeEnable=false;
	/**
	 * 服务降级分类
	 */
	private DegradeType degradeType=DegradeType.FAULTTOLERANT;
	/**
	 * 服务降级策略类型
	 */
	private StrategyType strategyType=StrategyType.EXCEPTION;
	
	
	//$NON-NLS-幂等机制配置$
	/**
	 * 幂等机制开关
	 */
	private boolean idempotentEnable=false;
	/**
	 * 幂等数据过期周期
	 */
	private long expireCycle=1000*60*2;
	/**
	 * 幂等数据容量大小
	 */
	private int idempStorCapacity=10000;
	
	
	public NeuralConf() {
	}
	
	/**
	 * @param maxRetryNum 最大重试次数,默认为3次
	 * @param retryCycle 重试休眠周期,默认休眠10ms(强烈不建议设置为0,因为释放句柄资源需要一定的时间)
	 * @param mockEnable mock服务开关,默认为false(关闭)
	 */
	public NeuralConf(int maxRetryNum, long retryCycle, boolean mockEnable){
		this.maxRetryNum=maxRetryNum;
		this.retryCycle=retryCycle;
		this.mockEnable=mockEnable;
	}

	public String getNeuralId() {
		return neuralId;
	}

	public void setNeuralId(String neuralId) {
		this.neuralId = neuralId;
	}

	public Setter getSetter() {
		return setter;
	}

	public void setSetter(Setter setter) {
		this.setter = setter;
	}

	public double getPassRate() {
		return passRate;
	}

	public void setPassRate(double passRate) {
		this.passRate = passRate;
	}

	public int getMaxRetryNum() {
		return maxRetryNum;
	}

	public void setMaxRetryNum(int maxRetryNum) {
		this.maxRetryNum = maxRetryNum;
	}

	public long getRetryCycle() {
		return retryCycle;
	}

	public void setRetryCycle(long retryCycle) {
		this.retryCycle = retryCycle;
	}

	public boolean isMockEnable() {
		return mockEnable;
	}

	public void setMockEnable(boolean mockEnable) {
		this.mockEnable = mockEnable;
	}

	public boolean isFailNotifyEnable() {
		return failNotifyEnable;
	}

	public void setFailNotifyEnable(boolean failNotifyEnable) {
		this.failNotifyEnable = failNotifyEnable;
	}

	public boolean isCallbackEnable() {
		return callbackEnable;
	}

	public void setCallbackEnable(boolean callbackEnable) {
		this.callbackEnable = callbackEnable;
	}

	public boolean isDegradeEnable() {
		return degradeEnable;
	}

	public void setDegradeEnable(boolean degradeEnable) {
		this.degradeEnable = degradeEnable;
	}

	public DegradeType getDegradeType() {
		return degradeType;
	}

	public void setDegradeType(DegradeType degradeType) {
		this.degradeType = degradeType;
	}

	public StrategyType getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(StrategyType strategyType) {
		this.strategyType = strategyType;
	}

	public boolean isIdempotentEnable() {
		return idempotentEnable;
	}

	public void setIdempotentEnable(boolean idempotentEnable) {
		this.idempotentEnable = idempotentEnable;
	}

	public long getExpireCycle() {
		return expireCycle;
	}

	public void setExpireCycle(long expireCycle) {
		this.expireCycle = expireCycle;
	}

	public int getIdempStorCapacity() {
		return idempStorCapacity;
	}

	public void setIdempStorCapacity(int idempStorCapacity) {
		this.idempStorCapacity = idempStorCapacity;
	}

	@Override
	public String toString() {
		return "NeuralConf [neuralId=" + neuralId + ", setter=" + setter
				+ ", passRate=" + passRate + ", maxRetryNum=" + maxRetryNum
				+ ", retryCycle=" + retryCycle + ", mockEnable=" + mockEnable
				+ ", failNotifyEnable=" + failNotifyEnable
				+ ", callbackEnable=" + callbackEnable + ", degradeEnable="
				+ degradeEnable + ", degradeType=" + degradeType
				+ ", strategyType=" + strategyType + ", idempotentEnable="
				+ idempotentEnable + ", expireCycle=" + expireCycle
				+ ", idempStorCapacity=" + idempStorCapacity + "]";
	}
	
}
