package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;

public class TestArrayList3 {

	private final ArrayList<Boolean> list1 = new ArrayList<Boolean>();
	
	@ThreadConfined("thread1")
	public void method1() {
		list1.add(Boolean.TRUE);
	}
	
	@ThreadConfined("thread2") 
	public void method2() {
		// ERROR, synchronization required, conflict with method1
		list1.add(Boolean.TRUE);
	}
	
	@ThreadSafe
	public void method3() {
		//ERROR, synchronization required
		list1.add(Boolean.TRUE);
	}
		
	@ThreadSafe
	public void method4() {
		//OK, synchronized
		synchronized(list1) {
		   list1.add(Boolean.TRUE);
		}
	}
}