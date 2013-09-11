package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.*;

public class TestListFromArrayList {

	private final List<Boolean> list1 = new ArrayList<Boolean>();
	private List<Boolean> list2 = new ArrayList<Boolean>();
	
	public TestListFromArrayList() {}
	
	@ThreadSafe
	public void method1() {
		// ERROR, list is an ArrayList
		list1.add(null);
	}
	
	@ThreadSafe
	public void method2() {
		// OK, field is not marked final, not enough information
		// in the future, it would be nice if checkthread could detect this bug
		// by doing a 2nd pass through and counting the number of putfields
		list2.add(null);
	}
}
