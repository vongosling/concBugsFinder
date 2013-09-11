package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.*;

public class TestSimple2 {

	private Object f1 = new Object();
	private Object f2 = new Object();
	
	@ThreadSafe
	public void method1A() {	
		System.out.println("hello world");
		// ERROR: locking loop wrt method2
		synchronized(f1) {
			System.out.println("hello world");
            method1B();
		}
	}
	
	@ThreadSafe
	public void method1B() {
		System.out.println("hello world");
		method1C();
	}
	
	@ThreadSafe
	public void method1C() {
		System.out.println("hello world");
		synchronized(f2) {
		    System.out.println("Hello World");	
		}
	}
	
	@ThreadSafe
	public void method2A() {		
		// ERROR: locking loop wrt method2
		synchronized(f2) {
            method2B();
		}
	}
	
	@ThreadSafe
	public void method2B() {
		method2C();
	}
	
	@ThreadSafe
	public void method2C() {
		synchronized(f1) {
		    System.out.println("Hello World");	
		}
	}
}