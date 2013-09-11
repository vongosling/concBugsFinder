/*
Copyright (c) 2008 Joe Conti CheckThread.org

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

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThreadcheckerTestSuite {
    
	private static Test basicSuite() {
        TestSuite suite = new TestSuite();       
        suite.addTestSuite(TestCaseDebug.class);           
        return suite;	
	}
		
    private static Test fullSuite() {
        TestSuite suite = new TestSuite();
        
        // generic unit tests
        suite.addTestSuite(TestCase_Inherit_Annotations.class); 
        suite.addTestSuite(TestCase_NoAnnotation.class);
        
        // Thread Confined unit tests
        suite.addTestSuite(TestCase_ThreadConfined_Custom.class);
        suite.addTestSuite(TestCase_ThreadConfined_InvokingThreadSafe.class);
        suite.addTestSuite(TestCase_ThreadConfined_LineNumber.class);  
        suite.addTestSuite(TestCase_ThreadConfined_Precanned.class);
        suite.addTestSuite(TestCase_ThreadConfined_Swing.class);
        
        // ThreadSafe unit tests
        suite.addTestSuite(TestCase_ThreadSafe_InvokingThreadConfined.class); 
        suite.addTestSuite(TestCase_ThreadSafe_RaceCondition.class);

        // ThreadUnsafe unit tests
        suite.addTestSuite(TestCase_NotThreadSafe_InvokingThreadSafe.class);
        suite.addTestSuite(TestCase_NotThreadSafe_InvokingThreadConfined.class); 
        suite.addTestSuite(TestCase_NotThreadSafe.class);
        
        // deadlocks
        //suite.addTestSuite(TestCase_Deadlocks.class);
        
        // other unit tests
        suite.addTestSuite(TestCaseNoPackageClass.class);       
        suite.addTestSuite(TestCaseExamples.class);
        suite.addTestSuite(TestCaseBookExamples.class);          
        suite.addTestSuite(TestCase_Collections.class);
        suite.addTestSuite(TestCase_CustomAnnotation.class);
        
        return suite;
    }

    
    /**
     * Runs the test suite using the textual runner.
     * java -checkdir path/to/java/classes
     */
    public static void main(String[] args) throws Exception {
		String checkDir = null;
    	
		if(args.length>1 && args[0].equals("-checkdir")) {
			checkDir = args[1];
		} else {
			System.err.println("Missing application input argument '-checkdir' and value");
			System.exit(-1);
		}
		
    	TestUtil.setCheckDir(checkDir);

        // for debugging specific unit test points
        //junit.textui.TestRunner.run(basicSuite());
    	
    	// full test suite
    	junit.textui.TestRunner.run(fullSuite());
    }
}