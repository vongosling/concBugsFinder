package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.*;

public class TestSharedPrimitive_Write_Single_Lock_This_Implicit {
	
	// No ERROR, access to shared data is synchronized
	private int fSharedPrimitive = 1;
	private volatile int fSharedVolatilePrimitive = 2;
	
	@ThreadSafe
	public synchronized void methodSharedWrite1(int v) {
		// atomic putfield operation
		fSharedPrimitive = v;
		fSharedVolatilePrimitive = v;
	}
}