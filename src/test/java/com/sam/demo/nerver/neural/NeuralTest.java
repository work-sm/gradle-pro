package com.sam.demo.nerver.neural;

import com.sam.demo.nerver.common.exception.NeuralException;
import com.sam.demo.nerver.neural.entity.NeuralConf;
import com.sam.demo.nerver.neural.route.IRoute;
import com.sam.demo.nerver.neural.type.ExecuteType;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class NeuralTest {

	String reqRoute="这是请求报文信息";
	final String resRoute="这是响应报文信息";
	final String resMock="这是响应报文信息";
	
	/**
	 * 测试正常业务:重试0次后直接失败(不调用mock服务)
	 */
	@Test
	public void route() {
		NeuralConf conf = new NeuralConf(0, 10, false);
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				return resRoute;
			}
			public String mock(String neuralId, String req) throws Throwable {
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) {
				return cycle;//可以自行计算每次休眠时间
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public void callback(String neuralId, ExecuteType execType, String res) {
			}
		};
		Neural<String, String> command = new Neural<String, String>(conf, route, reqRoute);
		
		String result = command.execute();
		Assert.assertEquals(resRoute, result);
	}
	
	/**
	 * 测试正常业务:重试0次后直接失败,抛出异常
	 */
	@Test
	public void routeRuntimeException() {
		NeuralConf conf = new NeuralConf(0, 10, false);
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				throw new NeuralException("模拟业务异常");
			}
			public String mock(String neuralId, String req) throws Throwable {
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) {
				return cycle;//可以自行计算每次休眠时间
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public void callback(String neuralId, ExecuteType execType, String res) {
			}
		};
		Neural<String, String> command = new Neural<String, String>(conf, route, reqRoute);
		
		try {
			command.execute();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof HystrixRuntimeException);
			Assert.assertTrue(e.getCause() instanceof NeuralException);
		}
	}
	
	/**
	 * 测试mock服务:重试0次后调度mock服务完成
	 */
	@Test
	public void retry0MockTrue() {
		NeuralConf conf = new NeuralConf(0, 10, true);
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				throw new NeuralException("模拟业务异常");
			}
			public String mock(String neuralId, String req) throws Throwable {
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) {
				return cycle;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public void callback(String neuralId, ExecuteType execType, String res) {
			}
		};
		Neural<String, String> command = new Neural<String, String>(conf, route, reqRoute);
		
		String result = command.execute();
		Assert.assertEquals(resMock, result);
	}
	
	/**
	 * 测试mock服务：重试3次后调度mock进行完成
	 */
	@Test
	public void retry3MockTrue() {
		NeuralConf conf = new NeuralConf(3, 10, true);
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				throw new RuntimeException("异常测试");
			}
			public String mock(String neuralId, String req) throws Throwable {
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) {
				return cycle;//可以自行计算每次休眠时间
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public void callback(String neuralId, ExecuteType execType, String res) {
			}
		};
		Neural<String, String> command = new Neural<String, String>(conf, route, reqRoute);
		
		String result = command.execute();
		Assert.assertEquals(resMock, result);
	}
	
	/**
	 * 测试mock服务：重试2次后直接失败,且不抛出异常
	 */
	@Test
	public void retry2MockFalseExceptionFalse() {
		NeuralConf conf = new NeuralConf(2, 10, false);
		IRoute<String, String> route = new IRoute<String, String>() {
			public String route(String neuralId, String req) throws Throwable {
				throw new RuntimeException("异常测试");
			}
			public String mock(String neuralId, String req) throws Throwable {
				return resMock;
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long timestamp) {
				return cycle;//可以自行计算每次休眠时间
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public void callback(String neuralId, ExecuteType execType, String res) {
			}
		};
		Neural<String, String> command = new Neural<String, String>(conf, route, reqRoute);
		
		try {
			command.execute();
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof HystrixRuntimeException);
			Assert.assertTrue(e.getCause() instanceof NeuralException);
		}
	}

}
