package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestReadPrimitive {

	private int var1 = 1;
	private final int var2 = 1;
	
	@ThreadSafe
	public void method1() {
		// OKAY, immutable
		System.out.println(var1);
	}
	
	@ThreadSafe
	public void method2() {
		// OKAY, immutable
		System.out.println(var1);
	}
}
