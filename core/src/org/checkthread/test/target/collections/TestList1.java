/*
Copyright (c) 2009 Joe Conti CheckThread.org

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package org.checkthread.test.target.collections;

import org.checkthread.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class TestList1 {

	private final List<Boolean> list1 = new ArrayList<Boolean>();
	private final List<Boolean> list2 = new ArrayList<Boolean>();
	
	@ThreadSafe
	public void method1() {
		// OK, read only method
	    list1.get(0);
	}
	
	@ThreadSafe
	public void method2() {
		// Error, ArrayList is not threadsafe
		list2.add(Boolean.TRUE);
	}

	
	@ThreadSafe
	public void method3() {
		// OK, synchronized
		synchronized(list2) {
		   list2.add(Boolean.TRUE);
		}
	}
	
}
