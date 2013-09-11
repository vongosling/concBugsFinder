package org.checkthread.examples;

import org.checkthread.annotations.*;

import java.util.List;
import java.util.ArrayList;

public class CollectionsRaceCondition1ERRORA {

	private final static List<Boolean> sList = new ArrayList<Boolean>();
	
	public static void main(String []args) {
		
		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			
		    @ThreadConfined("thread1")
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
			
			@ThreadConfined("thread2")
			public void run() {
				while(true) {
					// ERROR
					// This will error at runtime due to "IndexOutOfBoundsException"
					// The root cause is concurrent modification of the list
					for(int n = 0; n<sList.size(); n++) {
						sList.get(n);
					}
				}		
			}
		});
		t1.start();
		t2.start();
	}
}
