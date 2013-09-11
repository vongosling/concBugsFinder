package org.checkthread.test.target.notthreadsafe;

import java.util.concurrent.locks.*;

import org.checkthread.annotations.*;

public class TestSharedClass {

	static class CallingClass1 {
		@NotThreadSafe(synchronize=Scope.CLASS)
		public static void methodThreadUnsafeClass() {
			TestBasicStatic.threadUnsafeClass();
		}
	}

	static class CallingClass2 {
		@NotThreadSafe(synchronize=Scope.CLASS)
		public static void methodThreadUnsafeClass() {
			TestBasicStatic.threadUnsafeClass();
		}
	}

	class CallingClass3 {

		private final ReentrantLock lock = new ReentrantLock();
		
		@ThreadSafe
		public void method1() {
			synchronized (CallingClass1.class) {
				synchronized (this) {
				     CallingClass1.methodThreadUnsafeClass(); // OK
				}
			}
		}

		@ThreadSafe
		public void method2() {
			synchronized (CallingClass2.class) {
				CallingClass2.methodThreadUnsafeClass(); // OK
			}
		}
		
		@ThreadSafe
		public void method3() {
		     lock.lock();  // block until condition holds
		     try {
		    	 CallingClass2.methodThreadUnsafeClass(); // OK  
		     } finally {
		       lock.unlock();
		     }
		}

		@ThreadSafe
		public void method3A() {
		     lock.lock();   
		     CallingClass2.methodThreadUnsafeClass(); // OK  
		     lock.unlock();
		}
		
		@ThreadSafe
		public void method3B() {
		     CallingClass2.methodThreadUnsafeClass(); // ERROR 
		     lock.lock(); 
		     lock.unlock();
		}

		@ThreadSafe
		public void method3C() {
		     lock.lock(); 
		     lock.unlock();
		     CallingClass2.methodThreadUnsafeClass(); // ERROR 
		}

		
		@ThreadSafe
		public void methodYY() {
			synchronized (CallingClass2.class) {
               System.out.println("Hello World");
			}
			CallingClass2.methodThreadUnsafeClass(); // ERROR
		}

		@ThreadSafe
		public void methodXX() {
			CallingClass2.methodThreadUnsafeClass(); // ERROR 
		}
	}
}
