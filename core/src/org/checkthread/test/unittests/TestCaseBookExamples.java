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

import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.TestCase;

import org.checkthread.main.*;

public class TestCaseBookExamples extends TestCase {

	protected void setUp() {}

	protected void tearDown() {}

	public void testCh02() {
		helpertestDir("/examples/javathreads/ch02");
	}
	
	public void xtestCh03() {
		helpertestDir("/javathreads/examples/ch03");
	}

	public void xtestCh04() {
		helpertestDir("/javathreads/examples/ch04");
	}
	
	void helpertestDir(String dir) {
		File checkPath = new File(TestUtil.getCheckDir() + dir);
		ArrayList<URI> path = new ArrayList<URI>();
		path.add(checkPath.toURI());
		
        InputBean inputBean = InputBean.newInstance();
		inputBean.setTargetPath(path);
		
		ICheckThreadMain checkThread = CheckThreadMainFactory.newInstance(inputBean);
	    try {
	    	checkThread.run();
	    } catch (CheckThreadException e) {
	    	e.printStackTrace();
	    }
	    ArrayList<ICheckThreadError> list = checkThread.getErrors();
	    for(ICheckThreadError errbean : list) {
	    	errbean.printErr();
	    }   
	}
}
	