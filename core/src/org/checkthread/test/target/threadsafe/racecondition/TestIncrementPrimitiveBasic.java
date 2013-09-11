package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestIncrementPrimitiveBasic {

	volatile private int var1 =1;

	@ThreadSafe 
	public void method1A() {
		// ERROR, read/write operation and no synchronization
		// Ref: Java Threads, page 43
		// For now, checkthread will *not* detect read/write race conditions
		// due to the large amount of false positives
		var1++; 
	}
}
