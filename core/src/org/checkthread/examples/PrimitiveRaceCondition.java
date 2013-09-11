package org.checkthread.examples;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple example demonstrating a race condition
 * when writing and reading primitive data
 *
 * On most platforms, the conditional check "val!=5" within
 * handleRequest will eventually equate to true.
 */
public class PrimitiveRaceCondition {

	private volatile int val;

	// invoked by multiple threads
	private void handleRequest() {
		val = 1; 
		assert(val!=1);
		val = 0;
	}
	
	public static void main(String[] args) {

		final PrimitiveRaceCondition c = new PrimitiveRaceCondition();
		
		Runnable runner = new Runnable() {
			public void run() {
				while (true) {
					c.handleRequest();
				}
			}
		};
		
		// create two threads that call handleRequest repeatedly
		Thread t1 = new Thread(runner);
		Thread t2 = new Thread(runner);
		t1.start();
		t2.start();
	}
}
