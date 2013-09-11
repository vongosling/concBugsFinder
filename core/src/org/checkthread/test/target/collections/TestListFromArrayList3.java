package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.*;

public class TestListFromArrayList3 {

	private final List<Boolean> list1;
	private final List<Boolean> list2;
	
	public TestListFromArrayList3() {
		list1 = new Vector<Boolean>();
		list2 = Collections.synchronizedList(new ArrayList<Boolean>());
	}
	
	@ThreadSafe
	public void method2() {
		// OKAY
		list1.add(null);
		
		// not enough information, checkthread ignores
		list2.add(null);
	}
}
