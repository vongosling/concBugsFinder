package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class TestList3 {

	private final List<Boolean> list1 = new ArrayList<Boolean>();
	
	@ThreadConfined("thread1")
	public void method1() {
		list1.add(Boolean.TRUE);
	}
	
	@ThreadConfined("thread2") 
	public void method2() {
		// Should be error, for now, checkthread will ignore java.util.List
		list1.add(Boolean.TRUE);
	}
	
	@ThreadSafe
	public void method3() {
		// Error, list1 is not thread safe
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