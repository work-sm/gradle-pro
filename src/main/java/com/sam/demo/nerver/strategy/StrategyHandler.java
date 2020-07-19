package com.sam.demo.nerver.strategy;

import com.sam.demo.nerver.neural.Neural;
import com.sam.demo.nerver.neural.entity.FaultTolerance;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.strategy.entity.StrategyDo;

/**
 * 容错核心拦截执行器
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class StrategyHandler<REQ, RES> {
	
	public RES execute(StrategyDo<REQ, RES> ftDo){
		if(ftDo.getFt()==null){
			ftDo.setFt(new FaultTolerance());
		}
		if(ftDo.getConf()==null){
			ftDo.setConf(new NeuralConf());
		}
		if(ftDo.getRoute()==null){
			throw new RuntimeException("The 'route' cannot be empty.");
		}
		
		//创建容错模块
		Neural<REQ, RES> ftModuler=new Neural<REQ, RES>(ftDo.getConf(), ftDo.getRoute(), ftDo.getReq());
		//同步调用
		RES res = ftModuler.execute();
		
		return res;
	}

}
