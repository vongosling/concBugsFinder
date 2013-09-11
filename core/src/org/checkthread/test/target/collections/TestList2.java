package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class TestList2 {

	private final List<Boolean> list1 = new ArrayList<Boolean>();
	
	@ThreadConfined("thread1")
	public void method1a() {
		list1.add(Boolean.TRUE);
	}

	@ThreadConfined("thread1")
	public void method1b() {
		// OK, same ThreadConfined name policy
		list1.add(Boolean.TRUE);
	}


	@ThreadConfined("thread2")
	public void method1c() {
		// OK, read only method
		list1.get(0);
	}
	
	@ThreadConfined("thread2") 
	public void method2() {
		// Should be error, for now, checkthread will ignore java.util.List
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