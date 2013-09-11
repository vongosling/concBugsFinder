package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestWritePrimitive {

	private int var = 1;
	
	@ThreadSafe
	public void method1() {
		// OKAY, variable is never read
		var = 2;
	}
}
