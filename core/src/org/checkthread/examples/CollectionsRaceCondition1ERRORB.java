package org.checkthread.examples;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CollectionsRaceCondition1ERRORB {

	private final static List<Boolean> sList = Collections.synchronizedList(new ArrayList<Boolean>());

	public static void main(String[] args) {

		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				System.out.println(Thread.currentThread().getName());
				while (true) {
					for (int n = 0; n < 100; n++) {
						sList.add(Boolean.TRUE);
					}
					sList.removeAll(sList);
				}
			}
		});

		// Thread 2
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				// ERRORS out, IndexOutOfBoundsException
				System.out.println(Thread.currentThread().getName());
				while (true) {
					for (int n = 0; n < sList.size(); n++) {
						try {
							sList.get(n);
						} catch (Exception e) {
							System.out.println("ERROR: " + e);
							System.exit(-1);
						}
					}
				}
			}
		});
		t1.start();
		t2.start();
	}
}
