package com.sam.demo.nerver.neural;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.common.exception.NeuralException;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.route.IRoute;
import com.sam.demo.nerver.neural.type.ExecuteType;
import com.netflix.hystrix.HystrixCommand;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 神经元(代码行数约200)
 * <p>
 * 服务调用方式:
 * 1.同步服务调用
 * 2.异步服务调用
 * 3.并行服务调用
 * 
 * @see 注意事项:每一次实例化只能使用一次
 * @author lry
 * @param <REQ> 请求对象
 * @param <RES> 响应对象
 */
public class Neural<REQ, RES> extends HystrixCommand<RES> {

	private static final Logger logger=LoggerFactory.getLogger(Neural.class);
	
	private final REQ req;//请求对象
	private final NeuralConf conf;//配置信息
	private final IRoute<REQ, RES> route;//路由器
	private final AtomicInteger retryTimes = new AtomicInteger();// 重试计数器
	private ConcurrentLinkedQueue<ExecuteType> callChain=new ConcurrentLinkedQueue<ExecuteType>();//记录调用链
	
	/**
	 * @param conf 神经元配置信息
	 * @param route 路由器
	 * @param req 请求对象
	 */
	public Neural(NeuralConf conf, IRoute<REQ, RES> route, REQ req) {
		super(conf.getSetter());
		this.req = req;
		this.conf = conf;
		this.route = route;
	}
	
	/**
	 * 获取调用链
	 * @return
	 */
	public ConcurrentLinkedQueue<ExecuteType> getCallChain() {
		return callChain;
	}

	/**
	 * 依赖模块
	 */
	protected RES run() throws Exception {
		RES res=null;
		try {
			res = route.route(conf.getNeuralId(), req);
			callChain.add(ExecuteType.RUN_ROUTE_SUCCESS);/**调用链接1**/
			return res;
		} catch (Throwable t) {
			logger.error("Run route is failure, error is ["+t.getMessage()+"]",t);
			t.printStackTrace();

			try {//失败通知
				callChain.add(ExecuteType.RUN_ROUTE_FAILURE);/**调用链接2**/
				route.failNotify(conf.getNeuralId(), ExecuteType.RUN_ROUTE_FAILURE, req, t);
				callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接3**/
			} catch (Throwable fn) {
				callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接4**/
				logger.error("Run route failNotify is fail, error is "+fn.getMessage(),fn);
				fn.printStackTrace();
			}
			
			if (conf.getMaxRetryNum() < 1 && conf.getMaxRetryNum() != -1) {//不进入容错模式
				if (conf.isMockEnable()) {// 检查mock开关
					try {
						if(logger.isInfoEnabled()){
							logger.info("Run mockEnable is open, please waitting.. ");
						}
						res = route.mock(conf.getNeuralId(), req);// 调用mock服务进行返回
						callChain.add(ExecuteType.RUN_MOCK_SUCCESS);/**调用链接5**/
						return res;
					} catch (Throwable e) {
						logger.error("Run mock is failure, error is ["+e.getMessage()+"]",e);
						e.printStackTrace();
							
						try {//失败通知
							callChain.add(ExecuteType.RUN_MOCK_FAILURE);/**调用链接6**/
							route.failNotify(conf.getNeuralId(), ExecuteType.RUN_MOCK_FAILURE, req, e);
							callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接7**/
						} catch (Throwable fn) {
							callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接8**/
							logger.error("Run mock failNotify is fail, error is "+fn.getMessage(),fn);
							fn.printStackTrace();
						}
						throw new NeuralException(e);//必须向外抛出mock失败异常
					}
				}
			}
			// 进入容错重试,-1表示无限重试
			throw new NeuralException(t);//必须向外抛出route失败异常
		}
	}

	/**
	 * 失败容错
	 */
	protected RES getFallback() {
		RES res=null;
		int retryFTNum = 0;// 已经重试容错次数
		long startTime=SystemClock.now();//记录容错重试开始时间
		
		if(conf.getMaxRetryNum()<1 && conf.getMaxRetryNum() != -1){
			throw new NeuralException("Run route is failure, and maxRetryNum="+conf.getMaxRetryNum(),getExecutionException());
		}
		
		while (retryFTNum < conf.getMaxRetryNum() || conf.getMaxRetryNum() == -1) {// 循环进入容错重试流
			try {
				retryFTNum = retryTimes.addAndGet(1);// 容错执行次数
				if(logger.isInfoEnabled()){
					logger.info("Is fallback route retry "+retryFTNum+" times, please waitting.. ");
				}
				
				try {//呼吸周期计算并呼吸
					long sleepTime = route.breathCycle(conf.getNeuralId(), retryFTNum,
							conf.getMaxRetryNum(), conf.getRetryCycle(), SystemClock.now()-startTime);// 计算本次容错休眠时间
					callChain.add(ExecuteType.BREATHCYCLE_SUCCESS);/**调用链接9**/
					if (sleepTime > 0) {
						Thread.sleep(sleepTime);
					}else{
						if(logger.isDebugEnabled()){
							logger.debug("The breathCycle less than 1ms, recommended a greater than 0");
						}
					}					
				} catch (Throwable t) {
					logger.error("Fallback breathCycle is failure, error is ["+t.getMessage()+"]",t);
					t.printStackTrace();
					
					try {//失败通知
						callChain.add(ExecuteType.BREATHCYCLE_FAILURE);/**调用链接10**/
						route.failNotify(conf.getNeuralId(), ExecuteType.BREATHCYCLE_FAILURE, req, t);
						callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接11**/
					} catch (Throwable fn) {
						callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接12**/
						logger.error("Fallback breathCycle failNotify is fail, error is "+fn.getMessage(),fn);
						fn.printStackTrace();
					}
				}
				
				res = route.route(conf.getNeuralId(), req);
				callChain.add(ExecuteType.FALLBACK_ROUTE_SUCCESS);/**调用链接13**/
				return res;
			} catch (Throwable t) {
				logger.error("Fallback route is failure, error is ["+t.getMessage()+"]",t);
				t.printStackTrace();
			
				try {//失败通知
					callChain.add(ExecuteType.FALLBACK_ROUTE_FAILURE);/**调用链接14**/
					route.failNotify(conf.getNeuralId(), ExecuteType.FALLBACK_ROUTE_FAILURE, req, t);
					callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接15**/
				} catch (Throwable fn) {
					callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接16**/
					logger.error("Fallback route failNotify is fail, error is "+fn.getMessage(),fn);
					fn.printStackTrace();
				}
					
				if (conf.getMaxRetryNum() == -1) {// 进入无限循环状态
					if(logger.isDebugEnabled()){
						logger.debug("The value of the current -1 is maxRetryNum, and the state of infinite loop retry is not recommended.");
					}
					continue;
				} else if (retryFTNum >= conf.getMaxRetryNum()) {// 重试完毕,则该退出了
					if(logger.isInfoEnabled()){
						logger.info("Fallback route retry already finished.");
					}
					if (conf.isMockEnable()) {// 检查mock开关
						try {
							if(logger.isInfoEnabled()){
								logger.info("Fallback mockEnable is open, please waitting.. ");
							}
							
							res = route.mock(conf.getNeuralId(), req);// 调用mock服务进行返回
							callChain.add(ExecuteType.FALLBACK_MOCK_SUCCESS);/**调用链接17**/
							return res;
						} catch (Throwable e) {
							logger.error("Fallback mock is failure, error is ["+e.getMessage()+"]",e);
							e.printStackTrace();
							
							try {//失败通知
								callChain.add(ExecuteType.FALLBACK_MOCK_FAILURE);/**调用链接18**/
								route.failNotify(conf.getNeuralId(), ExecuteType.FALLBACK_MOCK_FAILURE, req, e);
								callChain.add(ExecuteType.FAILNOTIFY_SUCCESS);/**调用链接19**/
							} catch (Throwable fn) {
								callChain.add(ExecuteType.FAILNOTIFY_FAILURE);/**调用链接20**/
								logger.error("Fallback mock failNotify is fail, error is "+fn.getMessage(),fn);
								fn.printStackTrace();
							}
							throw new NeuralException(e);
						}
					} else {
						if(logger.isInfoEnabled()){
							logger.info("Fallback mockEnable is close.");
						}
					}
					throw new NeuralException(t);
				}
			}
		}
		return res;
	}
	
}
