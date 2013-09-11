package org.checkthread.test.unittests;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.threadconfined.invokingthreadsafe.*;

public class TestCase_ThreadConfined_InvokingThreadSafe extends TestCase{
	protected void setUp() {}

	protected void tearDown() {}

	public void testBasic1() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestBasic1.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 1;
		assertEquals(expectedValue, actualValue);
	}
	
	public void testBasic2() {
		TestParseHandler handler = TestUtil.parseClassHelper(TestBasic2.class);
		ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

		// Verify that there is one thread policy error
		int actualValue = list.size();
		int expectedValue = 0;
		assertEquals(expectedValue, actualValue);
	}
}
