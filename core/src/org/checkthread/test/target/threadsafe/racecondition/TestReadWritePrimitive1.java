package org.checkthread.test.target.threadsafe.racecondition;

import org.checkthread.annotations.*;

import java.util.concurrent.locks.*;

public class TestReadWritePrimitive1 {

	private volatile int var = 1;
	private ReentrantLock lock = new ReentrantLock();
	private volatile int var1 = 1;

	@ThreadSafe
	public void methodA() {
		// OKAY for now, get/put
		// Eventually it would be nice if checkthread could
		// detect the race condition without a large amount of false positives
		if(var1>0) {
			var1 = 1;
		}
	}
	
	@ThreadSafe
	public void methodC() {
		// OKAY, get/put
		System.out.println(var1);
		var1 = 1;
	}
	
	@ThreadSafe
	public void methodB() {
		// ERROR, write/read (put/get)
		var1 = 1;
		if(var1==1) {
			System.out.println("hello world");
		}
	}
	
	@ThreadSafe
	public void method1() {
		//ERROR, write/read (put/get)
		var = 3;
		System.out.println(var);
	}
	
	
	@ThreadSafe
	public void method1G() {
		// (ERROR - false positive) 
		// Okay but CheckThread will error because it can't detect that
		// lockwrapper() synchronizes. To mitigate this false positive, the
		lockwrapper();
		var = 3;
		System.out.println(var);
		unlockwrapper();
	}

	@ThreadSafe
	public synchronized void method1B() {
		//OKAY, synchronized
		var = 3;
		System.out.println(var);
	}

	@ThreadSafe
	public void method1C() {
		//OKAY, synchronized
		synchronized(this) {
		   var = 3;
		   System.out.println(var);
		}
	}
	
	@ThreadSafe
	public void method1D() {
		// Okay (true negative) but this should ideally ERROR
		// CheckThread should throw an error here
		// synchronizing on a local variable doesn't have any effect.
		Object foo = new Object();
		synchronized(foo) {
		   var = 3;
		   System.out.println(var);
		}
	}

	@ThreadSafe
	public void method1E() {
		// Okay
		ReentrantLock rl = new ReentrantLock();
		rl.lock();
		var = 3;
		System.out.println(var);
		rl.unlock();
	}
	
	@ThreadSafe
	public void method1F() {
		// OKAY
		lock.lock();
		var = 3;
		System.out.println(var);
		lock.unlock();
	}

	
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method2() {
		// OKAY, method requires synchronization
		var = 3;
		System.out.println(var);
	}
	
	private void lockwrapper() {
		lock.lock();
	}
	
	private void unlockwrapper() {
		lock.unlock();
	}
	
}
