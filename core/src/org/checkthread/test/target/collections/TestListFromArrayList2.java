package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.*;

public class TestListFromArrayList2 {

	private final List<Boolean> list1;

	public TestListFromArrayList2() {
		list1 = new ArrayList<Boolean>();
	}
	
	@ThreadSafe
	public void method2() {
		// ERROR, list is an arraylist
		list1.add(null);
	}
}
