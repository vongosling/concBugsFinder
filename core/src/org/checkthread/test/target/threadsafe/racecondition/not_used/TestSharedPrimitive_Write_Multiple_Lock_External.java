package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.ThreadSafe;

public class TestSharedPrimitive_Write_Multiple_Lock_External {

	// ERROR 
	// Access to shared data is synchronized
	// with different locks
	private int fSharedPrimitive = 1;
	private volatile int fSharedVolatilePrimitive = 2;
	
	// Locks
	private Object fLock1 = new Object();
	private Object fLock2 = new Object();
	
	@ThreadSafe
	public void methodSharedWrite1(int v) {
		synchronized (fLock1) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
	
	@ThreadSafe
	public void methodSharedWrite2(int v) {
		synchronized (fLock2) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
}