package org.checkthread.test.target.threadsafe.racecondition.not_used;

import org.checkthread.annotations.*;

public class TestSharedPrimitive1 {
	
	// ERROR: Unsynchronized data shared across multiple threads 
	// in 'methodSharedWrite' and 'methodSharedReadWrite'. 
	// Possible options:
	// 1) Confine this data to a specific thread. See
	// ThreadConfinedData for more info.
	// 2) Synchronize this data using synchronization (e.g. locks). See
	// a Java reference on synchronization for more information.
	private int fSharedPrimitive = 1;

	// ERROR: See above
	volatile int fSharedVolatilePrimitive = 2;
	
	@ThreadSafe
	public void methodSharedWrite(int v) {
		// atomic putfield operation
		fSharedPrimitive = v;
		fSharedVolatilePrimitive = v;
	}

	@ThreadSafe
	public void methodSharedRead() {
		// atomic getfield operation
		System.out.println(fSharedPrimitive);
		System.out.println(fSharedVolatilePrimitive);
	}
	
	@ThreadSafe
	public void methodSharedReadWrite(int v) {
		// not atomic, getfield, operation, then putfield
		fSharedPrimitive+=v;
		fSharedVolatilePrimitive+=v;
	}
}