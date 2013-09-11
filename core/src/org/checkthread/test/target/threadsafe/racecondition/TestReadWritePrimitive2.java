package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestReadWritePrimitive2 {

	private volatile int var = 1;

	@ThreadSafe
	public void method1(int v) {
		// OKAY, write only
		var = v;
	}

	@ThreadSafe
	public void method2() {
		// OKAY, read only
		System.out.println(var);
	}
	
	@ThreadSafe
	public void method3(int v) {
		//ERROR, read/write operation and no synchronization
		var = v;
		System.out.println(var);
	}
	
}
