package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.*;

public class TestDerivedList {

	private final List<Boolean> list1 = new ArrayList<Boolean>();

	public TestDerivedList() {}
	
	@ThreadSafe
	public void method2() {
		// Okay, list1 is synchronized
		// CheckThread currently doesn't support analysis of "java.util.List"
		list1.add(null);
	}
}
