package org.checkthread.test.target.deadlocks;

import org.checkthread.annotations.*;

public class TestSimple4 {

	private Object f2 = new Object();

	@ThreadSafe
	public synchronized void method1() {

		// ERROR: locking loop wrt method2
		synchronized (f2) {
			System.out.println("Hello World");
		}
	}

	@ThreadSafe
	public void method2() {

		// ERROR: locking loop wrt method1
		synchronized (f2) {
			synchronized (this) {
				System.out.println("Hello World");
			}
		}
	}
}
