package org.checkthread.test.target.notthreadsafe.invokingthreadsafe;

import java.util.concurrent.locks.*;
import org.checkthread.annotations.*;

public class TestNotThreadSafeWithThreadSafe {

	// The caller of this method must synchronize so that
	// all calls to this class instance occur on the same thread
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void notThreadSafeMethod() {
		methodThreadSafe(); // OK, calling thread safe method
	}
	
	@ThreadSafe
	public void methodThreadSafe() {
	   System.out.println("hello world");	
	}
	
	@ThreadSafe
	public void methodThreadSafe2() {
		notThreadSafeMethod(); // ERROR: calling not thread safe from thread safe
	}
	
	@ThreadSafe
	public synchronized void methodThreadSafe3() {
        notThreadSafeMethod(); // OK, method is synchronized
	}
	
	@ThreadSafe
	public void methodThreadSafe4() {
		synchronized(this) {
			notThreadSafeMethod(); // OK, call is synchronized
		}
	}

	@ThreadSafe
	public void methodThreadSafe5() {
		notThreadSafeMethod(); // ERROR, call is not synchronized
		synchronized(this) {
             System.out.println("Hello World");
		}
	}

	@ThreadSafe
	public void methodThreadSafe6() {
		synchronized(this) {
             System.out.println("Hello World");
		}	
		notThreadSafeMethod(); // ERROR, call is not synchronized
	}

	@ThreadSafe
	public void methodThreadSafe7() {
	     Lock  l= new ReentrantLock();
	     l.lock();
	     try {
	         notThreadSafeMethod(); // OK, 
	     } finally {
	         l.unlock();
	     }
	}
	
	@ThreadSafe
	public void methodThreadSafe8() {
	     Lock  l= new ReentrantLock();
	     l.lock();
	     try {
             System.out.println("Hello World");
	     } finally {
	         l.unlock();
	     }
         notThreadSafeMethod(); // ERROR, call is not synchronized
	}
		
}
