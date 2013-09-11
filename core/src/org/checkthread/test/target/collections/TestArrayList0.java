package org.checkthread.test.target.collections;


import org.checkthread.annotations.*;

import java.util.ArrayList;

public class TestArrayList0 {

	private final ArrayList<Boolean> list1= new ArrayList<Boolean>();
	
	@ThreadSafe
	public void method1() {
		// okay, read only
	    list1.get(0);
	}

	@ThreadSafe
	public void method2() {
		// okay, not shared data
		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(Boolean.TRUE);
	}
}
