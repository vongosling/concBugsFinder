package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.*;

public class TestSharedPrimitive_Write_Single_Lock_This_Explicit {

	// No error, access to shared data is synchronized
	// using the same instance lock
	private int fSharedPrimitive = 1;
	private volatile int fSharedVolatilePrimitive = 2;
    private final Object fLock = this;
    
	@ThreadSafe
	public void methodSharedWrite1(int v) {
		synchronized (this) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
	
	@ThreadSafe
	public void methodSharedWrite2(int v) {	
		synchronized (fLock) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
	
	public Object get() {return null;}
	
	@ThreadSafe
	public void methodSharedWrite3(int v) {	
		Object lock = this;
		Object lock2 = get();
		synchronized (lock) {
			// atomic putfield operation
			fSharedPrimitive = v;
			fSharedVolatilePrimitive = v;
		}
	}
}