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

package org.checkthread.test.unittests;

import java.util.*;
import junit.framework.TestCase;

import org.checkthread.config.*;
import org.checkthread.main.ICheckThreadError;

public class TestCaseNoPackageClass extends TestCase {
    
    protected void setUp() {}

	protected void tearDown() {}

	public void testNoAnnotation() {

		Class clazz = null;
		try {
			clazz = Class.forName("NoPackageClass");
		} catch (Exception e) {
			Log.reportException(e);
		}

		if (clazz != null) {
			TestParseHandler handler = TestUtil.parseClassHelper(clazz);
			ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

			// Verify that there is one thread policy error
			int actualValue = list.size();
			int expectedValue = 1;
			assertEquals(expectedValue, actualValue);

       }
	}
}