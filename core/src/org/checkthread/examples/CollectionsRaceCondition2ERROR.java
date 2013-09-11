package org.checkthread.examples;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CollectionsRaceCondition2ERROR {

	private final static List<Boolean> sList = new ArrayList<Boolean>();
	
	public static void main(String []args) {
		
		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			public void run() {
                while(true) {
                	for(int n = 0; n<100; n++) {
                	    sList.add(Boolean.TRUE);
                	}
                	sList.removeAll(sList);
                }
			}
		});
		
		// Thread 2
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				while (true) {
					// This will error at runtime due to a ConcurrentModificationException
					// Synchronizing on sList doesn't help since the List doesn't
					// use Collections.synchronizedList(...)
					synchronized (sList) {
						Iterator<Boolean> it = sList.iterator();
						while (it.hasNext()) {
							it.next();
						}
					}
				}		
			}
		});
		t1.start();
		t2.start();
	}
}
