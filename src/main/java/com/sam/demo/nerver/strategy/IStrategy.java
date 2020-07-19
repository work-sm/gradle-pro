package com.sam.demo.nerver.strategy;

import com.sam.demo.nerver.strategy.entity.StrategyDo;

/**
 * <b style="color:red;font-size:20px">容错策略</b>
 * <p>
 * 1.故障转移(Failover)<p>
 * 2.快速失败(Failfast)<p>
 * 4.失败缓存(Failcache)<p>
 * 5.失败通知(Failback)<p>
 * <p>
 * @author lry
 * @param <REQ>
 * @param <RES>
 */
public interface IStrategy<REQ, RES> {

	/**
	 * 容错执行
	 * 
	 * @param ftDo
	 * @return
	 */
	RES execute(StrategyDo<REQ, RES> ftDo);
	
}
