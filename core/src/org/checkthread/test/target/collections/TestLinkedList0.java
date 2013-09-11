package org.checkthread.test.target.collections;

import java.util.LinkedList;

import org.checkthread.annotations.ThreadSafe;

public class TestLinkedList0 {
	private final LinkedList<Boolean> list= new LinkedList<Boolean>();
	
	@ThreadSafe
	public void method1() {
		// okay, read only
	    list.get(0);
	}

	@ThreadSafe
	public void method2() {
		// ERROR, modifying structure not thread safe
		list.add(Boolean.TRUE);
		list.addAll(null);
		list.addFirst(null);
		list.addLast(null);
		list.clear();
		list.remove();
	}
}
