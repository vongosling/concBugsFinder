package org.checkthread.test.target.collections;

import java.util.HashMap;

import org.checkthread.annotations.ThreadSafe;

public class TestHashMap0 {
	private final HashMap<String,Boolean> map = new HashMap<String,Boolean>();
	
	@ThreadSafe
	public void method1() {
		// okay, read only
	    map.get(null);
	    
	    // okay if there is already a mapping for key
	    // ambiguous, marking as thread safe
	    map.put(null,Boolean.TRUE);
	}

	@ThreadSafe
	public void method2() {
		// ERROR, modifying structure not thread safe
		map.putAll(null);
		map.remove(null);
		map.clear();
	}
}