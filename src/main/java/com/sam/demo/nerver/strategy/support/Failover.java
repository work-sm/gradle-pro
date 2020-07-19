package com.sam.demo.nerver.strategy.support;

import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.strategy.IStrategy;
import com.sam.demo.nerver.strategy.StrategyHandler;
import com.sam.demo.nerver.strategy.entity.StrategyDo;

/**
 * 1.故障转移(Failover)<p>
 * 失败自动切换，当出现失败，重试其它服务器<p>
 * 通常用于读操作，但重试会带来更长延迟<p>
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class Failover<REQ, RES> implements IStrategy<REQ, RES> {

	@Override
	public RES execute(StrategyDo<REQ, RES> ftDo) {
		NeuralConf conf=ftDo.getConf();
		if(conf==null){
			conf=new NeuralConf(1, 0, true);
		}
		ftDo.setConf(new NeuralConf(1, conf.getMaxRetryNum(), conf.isMockEnable()));//失败转移最多重试3次,每次重试休眠10ms
		StrategyHandler<REQ, RES> handler=new StrategyHandler<REQ, RES>();
		
		return handler.execute(ftDo);
	}

}
