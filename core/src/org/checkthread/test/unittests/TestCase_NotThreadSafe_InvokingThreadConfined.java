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

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.notthreadsafe.invokingthreadconfined.*;

public class TestCase_NotThreadSafe_InvokingThreadConfined extends TestCase {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void test1() {       
        TestParseHandler handler = TestUtil.parseClassHelper(ConfinedCallingClassUnsafe.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify that there are no thread policy errors
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void test2() {       
        TestParseHandler handler = TestUtil.parseClassHelper(ConfinedCallingInstanceUnsafe.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
      
    public void test3() {       
        TestParseHandler handler = TestUtil.parseClassHelper(MultiConfinedCallingUnsafe.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }    
    
    public void test4() {       
        TestParseHandler handler = TestUtil.parseClassHelper(UnsafeCallingConfined.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify that there are no thread policy errors
        int actualValue = list.size();
        int expectedValue = 3;
        assertEquals(expectedValue,actualValue);
    }   
}