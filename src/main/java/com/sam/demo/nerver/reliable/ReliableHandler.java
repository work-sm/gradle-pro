package com.sam.demo.nerver.reliable;

/**
 * 高可用模块
 * 
 * @author lry
 */
public class ReliableHandler implements IReliable {

	@Override
	public void execute() {
	}

	@Override
	public boolean check() {
		return false;
	}

	@Override
	public void reconnect() {
	}

	@Override
	public void mcacherc() {
	}

	@Override
	public void release() {
	}

	@Override
	public void rhandshake() {
	}

}
