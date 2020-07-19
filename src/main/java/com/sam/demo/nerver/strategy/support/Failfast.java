package com.sam.demo.nerver.strategy.support;

import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.strategy.IStrategy;
import com.sam.demo.nerver.strategy.StrategyHandler;
import com.sam.demo.nerver.strategy.entity.StrategyDo;

/**
 * 2.快速失败(Failfast)
 * <p>
 * 快速失败，只发起一次调用，失败立即报错<p>
 * 通常用于非幂等性的写操作，比如新增记录<p>
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class Failfast<REQ, RES> implements IStrategy<REQ, RES> {

	public RES execute(StrategyDo<REQ, RES> ftDo) {
		ftDo.setConf(new NeuralConf(1, 0, true));//不进行重试、不切换mock服务
		StrategyHandler<REQ, RES> handler=new StrategyHandler<REQ, RES>();
		return handler.execute(ftDo);
	}

}
