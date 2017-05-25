package com.aura.input;

public interface Event {

	public static final int HANDLED   = 0x0;
	public static final int UNHANDLED = 0x1;

	public int invoke();

	public void fire();

	public boolean isRelease();

}
