package com.sam.demo.nerver.qos;

import com.sam.demo.nerver.qos.entity.QosOrder;
import com.sam.demo.nerver.qos.type.QosLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QosTest {

	public static void main(String[] args) throws Exception {
		List<QosOrder> orders = new ArrayList<QosOrder>();
		orders.add(new QosOrder("CHANNEL", true, 20, 200, 10));
		orders.add(new QosOrder("CHANNEL,SERVICE", true, 20, 200, 10));
		orders.add(new QosOrder("CHANNEL,SESSION,ADDRESS", true, 20, 200, 10));

		Qos.INSTANCE.init(orders);

		for (int i = 0; i < 1000; i++) {
			Map<QosLevel, String> map = new HashMap<QosLevel, String>();
			map.put(QosLevel.CHANNEL, "app" + System.currentTimeMillis());
			map.put(QosLevel.SERVICE, "100000" + System.currentTimeMillis());
			map.put(QosLevel.SCENARIO, "10" + System.currentTimeMillis());
			map.put(QosLevel.SESSION, "sessionId" + System.currentTimeMillis());
			map.put(QosLevel.ADDRESS, "lry.MacBook" + System.currentTimeMillis());
			map.put(QosLevel.HOST, "10.24.1.22" + System.currentTimeMillis());

			boolean falg = Qos.INSTANCE.checks(map);
			System.out.println("校验结果(" + i + "):" + falg);
			if (!falg) {
				break;
			}
			Thread.sleep(1);
		}
	}


}
