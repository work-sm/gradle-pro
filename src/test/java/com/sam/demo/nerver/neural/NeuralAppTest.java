package com.sam.demo.nerver.neural;

import com.sam.demo.nerver.Nerver;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.entity.RouteExpendProxy;
import com.sam.demo.nerver.neural.route.IRoute;
import com.sam.demo.nerver.neural.type.ExecuteType;
import org.junit.Test;

import java.util.Random;

public class NeuralAppTest {

	
	String reqRoute="这是请求报文信息";
	final String resRoute="这是响应报文信息";
	final String resMock="这是响应报文信息";
	
	/**
	 * 测试正常业务:重试0次后直接失败(不调用mock服务)
	 */
	@Test
	public void route() throws Throwable {
		NeuralConf conf = new NeuralConf( 3, 10, true);//不容错,但打开mock开关
		conf.setCallbackEnable(true);//设置异步响应开关
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(50));//模拟业务执行
				throw new RuntimeException();//模拟失败
			}
			public String mock(String neuralId, String req) throws Throwable {
				Thread.sleep(new Random().nextInt(30));//模拟业务执行
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) throws Throwable {
				Thread.sleep(new Random().nextInt(30));
				return 0;//可以自行计算每次休眠时间
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) throws Throwable {//如果失败通知开关打开,则失败后会广播该方法
				System.out.println("失败信息:"+t.getMessage());
				Thread.sleep(new Random().nextInt(30));
			}
			public void callback(String neuralId, ExecuteType execType, String res) throws Throwable {//如果异步开关打开,则执行成功后会广播该方法
				System.out.println("异步响应结果:"+res);
				Thread.sleep(new Random().nextInt(30));
			}
		};
		
		//创建代理
		RouteExpendProxy<String, String> rep = new Nerver<String, String>().getExpendProxy(route);
		NeuralHandler<String, String> command = new NeuralHandler<String, String>();
		for (int i = 0; i < 3; i++) {
			String result = command.handler(conf, rep, reqRoute);
			System.out.println("同步响应结果:"+result);
		}
	}
	
}
