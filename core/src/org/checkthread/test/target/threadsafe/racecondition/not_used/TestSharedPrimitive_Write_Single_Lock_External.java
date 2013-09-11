package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.*;

public class TestSharedPrimitive_Write_Single_Lock_External {

	// No error, access to shared data is synchronized
	private int fSharedPrimitive = 1;
	private volatile int fSharedVolatilePrimitive = 2;
	private Object fLock = new Object();

	@ThreadSafe
	public void methodSharedWrite1(int v) {
		synchronized (fLock) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
}