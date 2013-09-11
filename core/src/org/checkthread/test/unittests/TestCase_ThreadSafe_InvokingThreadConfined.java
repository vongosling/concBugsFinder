package org.checkthread.test.unittests;

import java.util.ArrayList;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.threadsafe.invokingthreadconfined.*;

import junit.framework.TestCase;

public class TestCase_ThreadSafe_InvokingThreadConfined  extends TestCase {
  
    protected void setUp() {}
    protected void tearDown() {}
    
    public void test1() {
        TestParseHandler handler = TestUtil.parseClassHelper(TestThreadSafe1.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
    }
    
    public void test2() {
        TestParseHandler handler = TestUtil.parseClassHelper(TestThreadSafe2.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void test3() {
        TestParseHandler handler = TestUtil.parseClassHelper(TestThreadSafeNestedRunnable.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void test4() {
        TestParseHandler handler = TestUtil.parseClassHelper(TestThreadSafeRunnable.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
}
