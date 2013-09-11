package org.checkthread.test.target.collections;


import org.checkthread.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class TestList0 {

	private final List<Boolean> list1= new ArrayList<Boolean>();
	
	@ThreadSafe
	public void method1() {
		// okay, read only
		list1.get(0);
	}

	@ThreadSafe
	public void method2() {
		// okay, not shared data
		List<Boolean> list = new ArrayList<Boolean>();
		list.add(Boolean.TRUE);
	}
}
