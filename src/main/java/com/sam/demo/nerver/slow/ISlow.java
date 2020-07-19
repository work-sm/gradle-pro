package com.sam.demo.nerver.slow;

/**
 * <b style="color:red;font-size:18px">慢性变化</b>
 * <p>
 * 在大流量的实时场景中,如果直接将一个数据立刻变化为另一个数据(两个数据有一定的差距),
 * 将会引起流量的大幅振动,更加糟糕的是数据的反复的进行大跨度切换,从而会导致整个链路的
 * 不稳定、增加故障发生的机率、无法提高吞吐量等的问题。
 * <p>
 * 因此该模块使用于处理数据变化的过度模块,使数据从缓慢到快的变化迁移。
 * 
 * @author lry
 *
 * @param <SYS>
 * @param <X>
 * @param <Y>
 */
public interface ISlow<SYS, X, Y> {
	
	/**
	 * 初始化系统参数
	 * 
	 * @param sys
	 */
	public void init(SYS sys);
	
	/**
	 * 函数计算
	 * 
	 * @param x
	 * @return
	 */
	public Y function(X x);
	
}
