package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.ThreadSafe;

public class TestSimple3 {

	private Object f1 = new Object();
	private Object f2 = new Object();
	
	@ThreadSafe
	public void method1() {
		
		// ERROR: locking loop wrt method2
		synchronized(f1) {
			method2();
		}
	}
	
	@ThreadSafe
	public void method2() {
		
		// ERROR: locking loop wrt method1
		synchronized(f2) {
			synchronized(f1) {
			    System.out.println("Hello World");	
			}
		}
	}
}