package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;

public class TestArrayList2 {

	private final ArrayList<Boolean> list1 = new ArrayList<Boolean>();

	@ThreadConfined("thread1")
	public void method1a() {
		list1.add(Boolean.TRUE);
	}
	
	@ThreadConfined("thread2")
	public void method1c() {
		// OK, read only method
		list1.get(0);
	}

	@ThreadConfined("thread2") 
	public void method2() {
		//ERROR: unsynchronized call to ThreaduUnsafe method
		// when another thread confined method is calling
		list1.add(Boolean.TRUE);
	}
	
	@ThreadConfined("thread1")
	public void method1b() {
		// OK, same ThreadConfined name policy
		list1.add(Boolean.TRUE);
	}

	@ThreadConfined("thread2") 
	public void method3() {
		//OK, synchronized
		synchronized(list1) {
		    list1.add(Boolean.TRUE);
		}
	}

}