package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.*;

public class TestSimple1B {

	private Object f1 = new Object();
	private Object f2 = new Object();
	
	@ThreadConfined("foo")
	public void method1() {
		
		// ERROR: locking loop wrt method2
		synchronized(f1) {
			synchronized(f2) {
			    System.out.println("Hello World");	
			}
		}
	}
	
	@ThreadConfined("bar")
	public void method2() {
		
		// ERROR: locking loop wrt method1
		synchronized(f2) {
			synchronized(f1) {
			    System.out.println("Hello World");	
			}
		}
	}
}