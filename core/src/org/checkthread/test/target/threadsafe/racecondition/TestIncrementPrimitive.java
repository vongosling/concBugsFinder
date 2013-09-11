package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

public class TestIncrementPrimitive {

	volatile private int var1 =1;
	private Object obj;
	
	@ThreadSafe 
	public void method1A() {
		// ERROR, read/write operation and no synchronization
		// Ref: Java Threads, page 43
		// For now, checkthread will *not* detect read/write race conditions
		// due to the large amount of false positives
		var1++; 
	}

	@ThreadSafe 
	public void method1B() {
		//ERROR, write/read race condition.
		var1 = 2; 
		System.out.println(var1);
	}
	
	@ThreadSafe 
	public synchronized void method1C() {
		//OK, method is synchronized
		var1++; 
	}
	
	@ThreadSafe 
	public void method1D() {
		synchronized(this) {
		    //OK, method is synchronized
		    var1++; 
		}
	}

	@ThreadSafe 
	public void method1E() {
		// OK
		synchronized(this) {
			var1 = 2; 
		}
		synchronized(this) {
			System.out.println(var1);
		}
	}
	
	@ThreadSafe
	public void method2A() {
		obj = new Object(); // OK, write only 
	}
	
	@ThreadSafe
	public void method2B() {
		obj = new Object(); 
		System.out.println(obj); // ERROR, write/read operation
	}
	
	@ThreadSafe
	public void method2() {
		var1 = 3; // OK, write only operation
	}

	@ThreadSafe
	public void method3() {
		int foo = var1; // OK, read only operation
	}
	
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method21() {
		// OKAY, method declares that it requires synchronization 
		var1++; 
	}
}
