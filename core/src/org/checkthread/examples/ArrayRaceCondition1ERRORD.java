package org.checkthread.examples;

public class ArrayRaceCondition1ERRORD {

	private final static int LENGTH = 100;
	private static Boolean[] sArray = new Boolean[LENGTH];
	private final static Object guard = new Object();
	
	private static void populate() {
		System.out.println("populate start");
    	for(int n = 0; n<100; n++) {
    	    sArray[n] = Boolean.TRUE;
    	}	
    	System.out.println("populate end");
	}
	
	private static void empty() {
		System.out.println("empty start");
    	for(int n = 0; n<100; n++) {
    	    sArray[n] = null;
    	}	
    	System.out.println("empty end");
	}
	
	public static void main(String []args) {
		
		populate();
		
		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			public void run() {
                while(true) {
                	synchronized(guard) {
                		populate();
                	}
                	synchronized(guard) {
                	    empty();
                	}
                }
			}
		});

		// Thread 2
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				while (true) {
					//System.out.println(sArray.length);
					// ERROR
					// This will error at runtime due to "NullPointerException"
					// The root cause is concurrent modification of the array
					synchronized (guard) {
						System.out.println(" *** print start");
						for (int n = 0; n < sArray.length; n++) {
							try {
							sArray[n].toString();
							} catch(NullPointerException e) {
								//System.out.println(n);
								e.printStackTrace();
								System.exit(1);
							}
						}
						System.out.println(" *** print end");
					}
				}
			}
		});
		t2.start();
		t1.start();
	}
}
