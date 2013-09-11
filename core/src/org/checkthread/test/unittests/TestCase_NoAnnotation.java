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
import org.checkthread.test.target.noannotation.*;
import org.checkthread.main.*;

public class TestCase_NoAnnotation extends TestCase {

	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testNoAnnotation() {
		TestParseHandler handler = TestUtil
				.parseClassHelper(TestNoAnnotationBasic.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 0;
		assertEquals(expectedValue, actualValue);
	}

	public void testNoAnnotationMethods() {
		TestParseHandler handler = TestUtil
				.parseClassHelper(TestNoAnnotationMethods.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 0;
		assertEquals(expectedValue, actualValue);
	}

	public void testNoAnnotationSuperclass() {
		TestParseHandler handler = TestUtil
				.parseClassHelper(TestNoAnnotationSubclass.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 0;
		assertEquals(expectedValue, actualValue);
	}
}
