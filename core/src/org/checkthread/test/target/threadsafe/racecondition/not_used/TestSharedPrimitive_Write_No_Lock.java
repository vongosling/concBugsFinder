package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.*;

public class TestSharedPrimitive_Write_No_Lock {
	
	//ERROR: shared data accessed without synchronization
	private int fSharedPrimitive = 1;
	private volatile int fSharedVolatilePrimitive = 2;
	
	@ThreadSafe
	public void methodSharedWrite(int v) {
		// According to "Java Threads", the following is an atomic 
		// putfield operation for non-double/long primitive (page 42). 
		// Throw error anyway to keep the user model simple and 
		// not varying depending on primitive datatypes.
		fSharedPrimitive = v;
		fSharedVolatilePrimitive = v;
	}
}