package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.ThreadConfined;

public class TestSimple1D {

	private Object f1 = new Object();
	private Object f2 = new Object();
	
	@ThreadConfined("foo")
	public void method1() {
		
		// OKAY, same locking order
		synchronized(f1) {
			synchronized(f2) {
			    System.out.println("Hello World");	
			}
		}
	}
	
	@ThreadConfined("foo")
	public void method2() {
		
		// OKAY, same locking order
		synchronized(f1) {
			synchronized(f2) {
			    System.out.println("Hello World");	
			}
		}
	}
}