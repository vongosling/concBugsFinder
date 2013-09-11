package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;

public class TestVector0 {

	private final Vector<Boolean> list1= new Vector<Boolean>();

	@ThreadSafe
	public void method1() {
		// okay, vector is synchronized
		list1.get(0);
		list1.add(Boolean.TRUE);
		list1.addElement(Boolean.TRUE);
		list1.clear();
		list1.addAll(null);
		list1.contains(null);
		list1.containsAll(null);
		list1.elements();
		list1.setSize(0);
	}

	@ThreadSafe
	public void method2() {
		// okay, not shared data
		Vector<Boolean> list = new Vector<Boolean>();
		list.add(Boolean.TRUE);
	}

	@ThreadSafe
	public void method3() {
		// ERROR, using an iterator is not thread safe
		Iterator i = list1.iterator();
		
		// ERROR, not thread safe
		ListIterator i2 = list1.listIterator();
		
		// ERROR, not thread safe
		ListIterator i3 = list1.listIterator(0);	
	}
}
