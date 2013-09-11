package org.checkthread.test.unittests;

/*
 Copyright (c) 2008 Joe Conti

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
import java.util.*;

import junit.framework.TestCase;
import org.checkthread.test.target.inherit_annotations.*;
import org.checkthread.main.*;

public class TestCase_Inherit_Annotations extends TestCase {

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testNoAnnotation() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestNoAnnotation.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 0;
		assertEquals(expectedValue, actualValue);
	}
	
	public void testThreadConfined() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestThreadConfined.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 3;
		assertEquals(expectedValue, actualValue);
	}
	
	public void testThreadSafe() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestThreadSafe.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 3;
		assertEquals(expectedValue, actualValue);
	}
	
	public void testThreadUnsafe() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestThreadUnsafe.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 4;
		assertEquals(expectedValue, actualValue);
	}
}