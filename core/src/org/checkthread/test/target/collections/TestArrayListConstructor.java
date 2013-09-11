package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;

public class TestArrayListConstructor {

	private final ArrayList<Boolean> list = new ArrayList<Boolean>();
	private final static ArrayList<Boolean> slist = new ArrayList<Boolean>();
	
	@ThreadSafe
	public TestArrayListConstructor() {
		// OKAY, local shared data in constructor
	    list.add(Boolean.TRUE);	
	    
	    // ERROR, static fields are shared data across constructors
	    slist.add(Boolean.TRUE);
	}

	@ThreadSafe
	public void method() {
		
		//okay, not shared data
		ArrayList<Boolean> list2 = new ArrayList<Boolean>();
		list2.add(Boolean.TRUE);
	}

}
