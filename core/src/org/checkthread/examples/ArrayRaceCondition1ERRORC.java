package org.checkthread.examples;

public class ArrayRaceCondition1ERRORC {

	private final static int LENGTH = 100;
	private static Boolean[] sArray = new Boolean[LENGTH];
	
	private static void populate() {
    	for(int n = 0; n<100; n++) {
    	    sArray[n] = Boolean.TRUE;
    	}	
	}
	
	
	private static void empty() {
    	for(int n = 0; n<100; n++) {
    	    sArray[n] = null;
    	}	
	}
	
	public static void main(String []args) {
		
		populate();
		
		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			public void run() {
                while(true) {
                	synchronized(sArray) {
                		populate();
                	}
                	synchronized(sArray) {
                	    empty();
                	}
                }
			}
		});
		
		// Thread 2
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				while(true) {
					System.out.println(sArray.length);
					// ERROR
					// This will error at runtime due to "NullPointerException"
					// The root cause is concurrent modification of the array
					for(int n = 0; n<sArray.length; n++) {
						synchronized(sArray) {
						    sArray[n].toString();
						}
					}
				}		
			}
		});
		t2.start();
		t1.start();
	}
}
