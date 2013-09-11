package org.checkthread.examples;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class CollectionsRaceCondition2OK {

	private final static List<Boolean> sList = Collections.synchronizedList(new ArrayList<Boolean>());
	
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
					//  OK
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
