package com.sam.demo.nerver.neural;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.common.exception.NeuralException;
import com.sam.demo.nerver.common.exception.PassRateException;
import com.sam.demo.nerver.neural.entity.Expend;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;
import com.sam.demo.nerver.neural.type.ExecuteType;
import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.HystrixCommandMetrics;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 神经元处理中心

 * @author lry
 * @param <REQ>
 * @param <RES>
 */
public class NeuralHandler<REQ, RES> {

	private static final Logger logger=LoggerFactory.getLogger(NeuralHandler.class);

	/**统计数据**/
	//$NON-NLS-整体成功率、失败率$
	//$NON-NLS-route/mock...成功率、失败率$
	
	//private ConcurrentHashMap<String, AtomicLong> rate=new ConcurrentHashMap<String, AtomicLong>();
	
	/**
	 * 放通率计算
	 */
	private  Random passRateRandom=new Random();
	/**
	 * 调用链
	 */
	private ConcurrentHashMap<String, AtomicLong> callChainMap=new ConcurrentHashMap<String, AtomicLong>();
	
	
	public ConcurrentHashMap<String, AtomicLong> getCallChainMap() {
		return callChainMap;
	}

	/**
	 * 处理中心
	 * 
	 * @param conf
	 * @param rep
	 * @param req
	 * @return
	 */
	public RES handler(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable {
		RES res=null;
		Expend expend=null;
		long startTime=0;
		Neural<REQ, RES> neural=null;
		HystrixCommandMetrics metrics=null;
		ConcurrentLinkedQueue<ExecuteType> callChain=null;
		try {
			if(passRateRandom.nextDouble()>conf.getPassRate()){//放通率控制
				throw new PassRateException("The rate of pass through rate is "
						+ "declined, the current rate(passRate) is "+(conf.getPassRate()*100)+"%.");
			}
			
			neural=new Neural<REQ, RES>(conf, rep.getRoute(), req);
			startTime=SystemClock.now();//总时间备忘录
			res=neural.execute();//执行神经元
			
			//后续处理
			afterHandler(conf, rep, req, res, expend, startTime, neural, metrics, callChain);
			
			return res;
		} catch (Throwable t) {
			//后续处理
			afterHandler(conf, rep, req, res, expend, startTime, neural, metrics, callChain);
			
			logger.error("NeuralHandler is failure, error is "+t.getMessage(),t);
			t.printStackTrace();
			Throwable tCause=t.getCause()==null?t:t.getCause();
			throw new NeuralException("NeuralHandler is failure, error is "+tCause.getMessage(),tCause);
		}
	}
	
	/**
	 * 后续处理中心(如异步回调、监控统计等)
	 */
	private RES afterHandler(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req, RES res, Expend expend, long startTime,
                             Neural<REQ, RES> neural, HystrixCommandMetrics metrics, ConcurrentLinkedQueue<ExecuteType> callChain) {
		//获取调用链
		callChain=new ConcurrentLinkedQueue<ExecuteType>();
		callChain.addAll(neural.getCallChain());

		if(conf.isCallbackEnable()){//异步回调响应,如果异步回调开关打开
			if(logger.isInfoEnabled()){
				logger.info("Run callbackEnable is open, is callback.. ");
			}
			try {
				rep.getRoute().callback(conf.getNeuralId(), neural.getCallChain().poll(), res);
				callChain.add(ExecuteType.CALLBACK_SUCCESS);/**调用链接21**/
			} catch (Throwable t) {
				t.printStackTrace();
				callChain.add(ExecuteType.CALLBACK_FAILURE);/**调用链接22**/
				
				try {//失败通知
					rep.getRoute().failNotify(conf.getNeuralId(), ExecuteType.CALLBACK_FAILURE, req, t);
					callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接23**/
				} catch (Throwable fn) {
					callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接24**/
					logger.error("Run mock failNotify is fail, error is "+fn.getMessage(),fn);
					fn.printStackTrace();
				}
				throw new NeuralException("NeuralHandler's route callback is failure, error is "+t.getMessage(),t);
			}
		}
		long allExpend=SystemClock.now()-startTime;//计算总耗时指标
		
		//获取指标
		expend=rep.getExpend();
		expend.setExpend(new AtomicLong(allExpend));
		metrics=neural.getMetrics();
		
		//$NON-NLS-后续统计$
		System.out.println("Nerver监控指标结果:"+expend);
		System.out.println("Hystrix监控指标结果:"+JSON.toJSONString(metrics));
		System.out.println("调用链("+callChain.size()+"):"+callChain);
		
		return res;
	}
	
}
