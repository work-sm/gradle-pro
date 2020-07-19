package com.sam.demo.nerver;

import com.sam.demo.nerver.common.SystemClock;
import com.sam.demo.nerver.common.exception.DegradeException;
import com.sam.demo.nerver.common.proxy.ProxyFactory;
import com.sam.demo.nerver.degrade.DegradeHandler;
import com.sam.demo.nerver.degrade.IBizDegrade;
import com.sam.demo.nerver.degrade.IDegrade;
import com.sam.demo.nerver.degrade.type.DegradeType;
import com.sam.demo.nerver.idempotent.Idempotent;
import com.sam.demo.nerver.idempotent.handler.IdempotentHandler;
import com.sam.demo.nerver.neural.NeuralHandler;
import com.sam.demo.nerver.neural.entity.Expend;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;
import com.sam.demo.nerver.neural.proxy.ExpendProxy;
import com.sam.demo.nerver.neural.route.IRoute;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 微服务神经元
 * 
 * @author lry
 *
 * @param <REQ> 请求对象
 * @param <RES> 响应对象
 */
public class Nerver<REQ, RES> {

	private NeuralHandler<REQ, RES> handler;
	private Idempotent<REQ, RES> idempotent;
	
	public Nerver() {
		handler=new NeuralHandler<REQ, RES>();
	}
	
	/**
	 * 神经请求(非服务降级)
	 * 
	 * @param conf
	 * @param route
	 * @param req
	 * @return
	 */
	public RES neural(NeuralConf conf, IRoute<REQ, RES> route, REQ req) {
		return neural(conf, route, null, req);
	}
	
	/**
	 * 神经请求(支持服务降级)
	 * 
	 * @param conf
	 * @param route
	 * @param req
	 * @return
	 */
	public RES neural(NeuralConf conf, IRoute<REQ, RES> route, final IBizDegrade<REQ, RES> bizDegrade, REQ req) {
		RES res=null;
		
		//$NON-NLS-1.幂等校验与返回$
		if(conf.isIdempotentEnable()){//幂等机制打开
			if(idempotent==null){
				idempotent=new IdempotentHandler<REQ, RES>(conf.getExpireCycle(), conf.getIdempStorCapacity());
			}
			try {
				res=idempotent.idempotent(conf.getNeuralId());//幂等缓存查找
				if(res!=null){
					return res;//幂等返回
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		try {
			//创建代理
			RouteExpendProxy<REQ, RES> rep = getExpendProxy(route);
			if(conf.isDegradeEnable()){//服务降级开关
				if(conf.getDegradeType()==DegradeType.BUSINESS&&bizDegrade==null){//校验
					throw new DegradeException("'degradeType' for business downgrade, so 'bizDegrade' can not be empty.");
				}
				
				//服务降级处理
				IDegrade<REQ, RES> degrade=new IDegrade<REQ, RES>() {
					public RES degrade(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable {
						return handler.handler(conf, rep, req);
					}
					public RES biz(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable {
						return bizDegrade.biz(conf, rep, req);
					}
				};
				
				DegradeHandler<REQ, RES> degradeCore=new DegradeHandler<REQ, RES>();//创建服务降级
				res=degradeCore.handler(conf, rep, req, degrade, bizDegrade);//执行服务降级过滤器
			}else{
				res=handler.handler(conf, rep, req);	
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		//$NON-NLS-2.幂等数据收集$
		if(conf.isIdempotentEnable()){//幂等机制打开
			try {
				idempotent.storage(conf.getNeuralId(), req, res);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	/**
	 * 代理路由器
	 * @param route
	 * @return
	 */
	public RouteExpendProxy<REQ, RES> getExpendProxy(IRoute<REQ, RES> route) throws Throwable {
		Expend expend=null;
		ExpendProxy<REQ, RES> hander = null;//耗时统计代理
		long pExpendStart=SystemClock.now();
		try {
			hander=new ExpendProxy<REQ, RES>();
			route = new ProxyFactory<IRoute<REQ, RES>, IRoute<REQ, RES>>().build(hander, route);
		} finally {
			expend=hander.getExpend();
			expend.setProxy(new AtomicLong(SystemClock.now()-pExpendStart));//计算代理创建耗时
			hander.setExpend(expend);
		}
		return new RouteExpendProxy<REQ, RES>(expend, route);
	}
	
}
