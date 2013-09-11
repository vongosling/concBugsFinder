package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestIncrementStaticPrimitiveConstructor {

	volatile private static int var;

	@ThreadSafe 
	public TestIncrementStaticPrimitiveConstructor() {
		
		// ERROR, shared data is static and shared across
		// all calls to constructors. Therefore, there
		// can be a write/read race condition.
		
		// For now, checkthread will *not* detect this error
		// true/negative
		var = 1;
		System.out.println(var);
	}
		
}
