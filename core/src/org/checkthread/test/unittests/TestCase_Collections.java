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

package org.checkthread.test.unittests;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.collections.*;

public class TestCase_Collections extends TestCase {
    protected void setUp() {}
    protected void tearDown() {}

    public void testList0() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestList0.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testList1() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestList1.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testList2() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestList2.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testList3() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestList3.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testArrayList0() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestArrayList0.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testArrayList1() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestArrayList1.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testArrayList2() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestArrayList2.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testArrayList3() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestArrayList3.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify
        int actualValue = list.size();
        int expectedValue = 2;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testVector0() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestVector0.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 3;
        assertEquals(expectedValue,actualValue);
    }

   
    public void testLinkedList0() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestLinkedList0.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 6;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testArrayListConstructor() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestArrayListConstructor.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
    
    public void testListFromVector() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestListFromArrayList.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    } 
    
    public void testListFromVector2() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestListFromArrayList2.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    } 
    
    public void testListFromVector3() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestListFromArrayList3.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
    } 
    private void displayError(ArrayList<ICheckThreadError> errList) {
    	for(ICheckThreadError err : errList) {
    		System.out.println(err.getErrorMessage());	
    	}
    }
}
