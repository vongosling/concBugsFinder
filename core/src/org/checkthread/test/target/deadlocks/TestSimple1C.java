package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.*;

public class TestSimple1C {

	private Object f1 = new Object();
	private Object f2 = new Object();
	
	@ThreadConfined("foo")
	public void method1() {
		
		// OKAY, called by same runtime thread
		synchronized(f1) {
			synchronized(f2) {
			    System.out.println("Hello World");	
			}
		}
	}
	
	@ThreadConfined("foo")
	public void method2() {
		
		// OKAY, called by same runtime thread
		synchronized(f2) {
			synchronized(f1) {
			    System.out.println("Hello World");	
			}
		}
	}
}