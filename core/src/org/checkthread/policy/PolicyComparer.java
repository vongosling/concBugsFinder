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

package org.checkthread.policy;

import org.checkthread.annotations.ThreadName;
import org.checkthread.main.ICheckThreadError;
import org.checkthread.parser.IInvokeMethodInfo;

public class PolicyComparer {
	 public static ICheckThreadError compareThreadPolicy(
	            IInvokeMethodInfo invokeInfo) {
	        
	        ICheckThreadError retval = null;
	        IThreadPolicy parentPolicy;
	        IThreadPolicy invokedPolicy;        
	        
	        if(invokeInfo.isStaticBlock()) {
	            parentPolicy = new ThreadConfinedPolicy(false, ThreadName.MAIN,true);	
	        } else {
	            parentPolicy = invokeInfo.getParentMethodInfo().getThreadPolicy();
	        }
	        
	        invokedPolicy = invokeInfo.getInvokedThreadPolicy();
	    
	        if (parentPolicy!=null && invokedPolicy!=null) {
	        	retval = parentPolicy.getError(invokedPolicy, invokeInfo);

	        } else if (parentPolicy==null) {
	           // For now, do nothing if the calling method has 
	           // no thread policy. We may decide to throw a warning
	           // or error in a future release.
	        } else if (invokedPolicy==null) {
	            // For now, do nothing if the invoked method has 
	            // no thread policy. We may decide to throw a warning
	            // or error in a future release.
	        }
	        
	        return retval;
	    }
	    
}
