package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestIncrementPrimitiveConstructor {

	volatile private int var;

	@ThreadSafe 
	public TestIncrementPrimitiveConstructor() {
		
		// Okay since shared data is owned by constructor
		// There is no way for multiple threads to access
		this.var = 1;
		System.out.println(this.var);
	}
		
}
