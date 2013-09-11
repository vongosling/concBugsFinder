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

import org.checkthread.test.target.collections.TestArrayList0;
import org.checkthread.test.target.collections.TestArrayList1;
import org.checkthread.test.target.collections.TestArrayList2;
import org.checkthread.test.target.collections.TestArrayListConstructor;
import org.checkthread.test.target.collections.TestLinkedList0;
import org.checkthread.test.target.collections.TestListFromArrayList;
import org.checkthread.test.target.collections.TestVector0;
import org.checkthread.test.target.deadlocks.*;
import org.checkthread.test.target.inherit_annotations.TestThreadConfined;
import org.checkthread.test.target.inherit_annotations.TestThreadSafe;
import org.checkthread.test.target.notthreadsafe.TestSharedClass;
import org.checkthread.test.target.notthreadsafe.invokingthreadconfined.ConfinedCallingInstanceUnsafe;
import org.checkthread.test.target.notthreadsafe.invokingthreadsafe.TestNotThreadSafeWithThreadSafe;
import org.checkthread.test.target.threadconfined.custom.*;
import org.checkthread.test.target.threadconfined.swing.TestBasicSwing;
import org.checkthread.test.target.threadconfined.swing.TestSwingExample1B;
import org.checkthread.test.target.threadsafe.racecondition.TestIncrementPrimitive;
import org.checkthread.test.target.threadsafe.racecondition.TestIncrementPrimitiveBasic;
import org.checkthread.test.target.threadsafe.racecondition.TestIncrementPrimitiveConstructor;
import org.checkthread.test.target.threadsafe.racecondition.TestIncrementStaticPrimitiveConstructor;
import org.checkthread.xmlpolicy.*;
import org.checkthread.deadlockdetection.LockAdjacencyListManager;
import org.checkthread.main.ICheckThreadError;

public class TestCaseDebug extends TestCase {
    
    protected void setUp() {}
	protected void tearDown() {}
    
	//Analysis of "java.util.List" not supported
    public void testListFromVector() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestListFromArrayList.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    } 
    
    private void displayError(ArrayList<ICheckThreadError> errList) {
    	for(ICheckThreadError err : errList) {
    		System.out.println("ERROR: " + err.getErrorMessage());	
    	}
    }
    
}