package org.checkthread.examples;

import java.util.List;
import java.util.ArrayList;

public class CollectionsRaceCondition1OKB {

	private final static List<Boolean> sList = new ArrayList<Boolean>();

	public static void main(String[] args) {

		// Thread 1
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				while (true) {
					synchronized (sList) {
						for (int n = 0; n < 100; n++) {
							sList.add(Boolean.TRUE);
						}
						sList.removeAll(sList);
					}
				}
			}
		});

		// Thread 2
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				while (true) {
					// OK
					synchronized (sList) {
						for (int n = 0; n < sList.size(); n++) {
							sList.get(n);
						}
					}
				}
			}
		});
		t1.start();
		t2.start();
	}
}
