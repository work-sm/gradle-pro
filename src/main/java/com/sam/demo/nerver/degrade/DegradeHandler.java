package com.sam.demo.nerver.degrade;

import com.sam.demo.nerver.common.exception.DegradeException;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;

/**
 * 服务降级
 * 
 * @author lry
 *
 * @param <REQ>
 * @param <RES>
 */
public class DegradeHandler<REQ, RES> {

	/**
	 * 服务降级处理中心
	 * 
	 * @param conf 请求配置
	 * @param route 路由器
	 * @param req 请求对象
	 * @param handler 服务降级处理
	 * @param bizDegrade 业务降级
	 * @return
	 * @throws Throwable
	 */
	public RES handler(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req,
			IDegrade<REQ, RES> degrade, IBizDegrade<REQ, RES> bizDegrade) throws Throwable {
		switch (conf.getDegradeType()) {
		case SHIELDING:// 屏蔽降级
			return strategyType(conf, rep, req);
		case FAULTTOLERANT:// 快速容错降级
			try {
				conf.setMaxRetryNum(0);// 关闭重试次数
				conf.setMockEnable(false);// 关闭mock开关

				// 关闭其他开关后直接执行route,从而达到快速失败然后进入容错降级模式
				return degrade.degrade(conf, rep, req);
			} catch (Throwable t) {
				return strategyType(conf, rep, req);// 直接失败,然后执行容错降级
			}
		case BUSINESS:// 业务降级
			return bizDegrade.biz(conf, rep, req);
		default:
			throw new DegradeException("'degradeType' is illegal type.");
		}
	}

	/**
	 * 容错策略
	 * 
	 * @param conf
	 * @param rep
	 * @param req
	 * @return
	 * @throws Throwable
	 */
	private RES strategyType(NeuralConf conf, RouteExpendProxy<REQ, RES> rep, REQ req) throws Throwable {
		switch (conf.getStrategyType()) {
		case NULL:// 返回null降级
			return null;
		case MOCK:// 调用mock降级
			return rep.getRoute().mock(conf.getNeuralId(), req);
		case EXCEPTION:// 抛异常降级
			throw new DegradeException("Service degradation: throw exception.");
		default:
			throw new DegradeException("'strategyType' is illegal type.");
		}
	}

}
