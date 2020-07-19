package com.sam.demo.nerver;

import com.sam.demo.nerver.common.exception.NeuralException;
import com.sam.demo.nerver.degrade.IBizDegrade;
import com.sam.demo.nerver.degrade.type.DegradeType;
import com.sam.demo.nerver.degrade.type.StrategyType;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;
import com.sam.demo.nerver.neural.route.IRoute;
import com.sam.demo.nerver.neural.type.ExecuteType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * 服务降级测试
 * 
 * @author lry
 */
public class NerverDegradeTest {

	String reqRoute="这是请求报文信息";
	final String resRoute="这是Route服务的响应报文信息";
	final String resMock="这是Mock服务的响应报文信息";
	final String resBiz="这是业务降级";
	
	/**
	 * 容错直接抛异常服务降级
	 * @throws Exception
	 */
	@Test
	public void degrade2Exception() throws Exception {
		IBizDegrade<String, String> bizDegrade=new IBizDegrade<String, String>() {
			public String biz(NeuralConf conf, RouteExpendProxy<String, String> rep, String req) throws Throwable {
				return resBiz;
			}
		};
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(50));//模拟业务执行
				throw new RuntimeException("模拟路由业务异常");//模拟失败
			}
			public String mock(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(30));//模拟业务执行
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) throws Throwable {
				return 0;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) throws Throwable {
				System.out.println("失败信息:"+t.getMessage());
				t.printStackTrace();
			}
			public void callback(String neuralId, ExecuteType execType, String res) throws Throwable {
			}
		};
		NeuralConf conf = new NeuralConf(0, 10, true);//不容错,但打开mock开关
		conf.setDegradeEnable(true);//设置异步响应开关
		new Nerver<String,String>().neural(conf, route, bizDegrade, reqRoute);
	}
	
	/**
	 * 容错直接直接Mock服务降级
	 * @throws Exception
	 */
	@Test
	public void degrade2Mock() throws Exception {
		IBizDegrade<String, String> bizDegrade=new IBizDegrade<String, String>() {
			public String biz(NeuralConf conf, RouteExpendProxy<String, String> rep, String req) throws Throwable {
				return resBiz;
			}
		};
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(50));//模拟业务执行
				throw new NeuralException("模拟路由业务异常");//模拟失败
			}
			public String mock(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(30));//模拟业务执行
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) throws Throwable {
				return 0;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) throws Throwable {
			}
			public void callback(String neuralId, ExecuteType execType, String res) throws Throwable {
			}
		};
		NeuralConf conf = new NeuralConf(0, 10, true);//不容错,但打开mock开关
		conf.setDegradeEnable(true);//设置异步响应开关
		conf.setStrategyType(StrategyType.MOCK);
		String result=new Nerver<String,String>().neural(conf, route, bizDegrade, reqRoute);
		Assert.assertEquals(result, resMock);
	}
	
	/**
	 * 容错直接直接业务服务降级
	 * @throws Exception
	 */
	@Test
	public void degrade2Biz() throws Exception {
		IBizDegrade<String, String> bizDegrade=new IBizDegrade<String, String>() {
			public String biz(NeuralConf conf, RouteExpendProxy<String, String> rep, String req) throws Throwable {
				return resBiz;
			}
		};
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(50));//模拟业务执行
				throw new NeuralException("模拟路由业务异常");//模拟失败
			}
			public String mock(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(30));//模拟业务执行
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) throws Throwable {
				return 0;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) throws Throwable {
			}
			public void callback(String neuralId, ExecuteType execType, String res) throws Throwable {
			}
		};
		NeuralConf conf = new NeuralConf(0, 10, true);//不容错,但打开mock开关
		conf.setDegradeEnable(true);//设置异步响应开关
		conf.setDegradeType(DegradeType.BUSINESS);
		conf.setStrategyType(StrategyType.MOCK);
		String result=new Nerver<String,String>().neural(conf, route, bizDegrade, reqRoute);
		Assert.assertEquals(result, resBiz);
	}
	
	/**
	 * 容错直接直接Mock服务降级
	 * @throws Exception
	 */
	@Test
	public void degrade2NULL() throws Exception {
		IBizDegrade<String, String> bizDegrade=new IBizDegrade<String, String>() {
			public String biz(NeuralConf conf, RouteExpendProxy<String, String> rep, String req) throws Throwable {
				return resBiz;
			}
		};
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(50));//模拟业务执行
				throw new NeuralException("模拟路由业务异常");//模拟失败
			}
			public String mock(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(30));//模拟业务执行
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) throws Throwable {
				return 0;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) throws Throwable {
			}
			public void callback(String neuralId, ExecuteType execType, String res) throws Throwable {
			}
		};
		NeuralConf conf = new NeuralConf(0, 10, true);//不容错,但打开mock开关
		conf.setDegradeEnable(true);//设置异步响应开关
		conf.setStrategyType(StrategyType.NULL);
		String result=new Nerver<String,String>().neural(conf, route, bizDegrade, reqRoute);
		Assert.assertEquals(result, null);
	}
	
}
