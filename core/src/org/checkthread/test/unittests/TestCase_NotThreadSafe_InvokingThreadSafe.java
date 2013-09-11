package org.checkthread.test.unittests;

import java.util.*;

import junit.framework.TestCase;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.notthreadsafe.invokingthreadsafe.*;

public class TestCase_NotThreadSafe_InvokingThreadSafe extends TestCase {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void test1() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestNotThreadSafeWithThreadSafe.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify that there are no thread policy errors
        int actualValue = list.size();
        int expectedValue = 4;
        assertEquals(expectedValue,actualValue);
    }
}