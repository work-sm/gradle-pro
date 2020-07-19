package com.sam.demo.nerver.neural.proxy;

import com.sam.demo.nerver.common.proxy.IProxyHandler;
import com.sam.demo.nerver.common.proxy.ProxyFactory;
import com.sam.demo.nerver.neural.route.IRoute;
import com.sam.demo.nerver.neural.type.ExecuteType;
import org.junit.Test;

import java.util.Random;

public class ExpendProxyTest {

	@Test
	public void buildTest() {
		IProxyHandler<IRoute<String, Boolean>> hander = new ExpendProxy<String, Boolean>();
		ProxyFactory<IRoute<String, Boolean>, IRoute<String, Boolean>> factory=new ProxyFactory<IRoute<String, Boolean>, IRoute<String, Boolean>>();
		
		IRoute<String, Boolean> req=new IRoute<String, Boolean>() {
			public Boolean route(String neuralId, String req) throws Throwable {
				return null;
			}
			public Boolean mock(String neuralId, String req) throws Throwable {
				System.out.println(neuralId+"--->"+req);
				Thread.sleep(new Random().nextInt(300));
				return false;
			}
			public void failNotify(String neuralId, ExecuteType execType, String req, Throwable t) {
			}
			public long breathCycle(String neuralId, int times, int maxRetryNum, long cycle, long expend) {
				return 0;
			}
			public void callback(String neuralId, ExecuteType execType, Boolean res) {
			}
		};
		try {
			factory.build(hander,req).mock("ididi", "ssss");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
