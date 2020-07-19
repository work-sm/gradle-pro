package com.sam.demo.nerver.strategy.support;

import com.sam.demo.nerver.strategy.IStrategy;
import com.sam.demo.nerver.strategy.entity.StrategyDo;

/**
 * 4.失败缓存(Failcache)<p>
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class Failcache<REQ, RES> implements IStrategy<REQ, RES> {

	@Override
	public RES execute(StrategyDo<REQ, RES> ftDo) {
		return null;
	}

}
