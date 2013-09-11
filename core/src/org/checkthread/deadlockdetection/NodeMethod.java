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

package org.checkthread.deadlockdetection;

import org.checkthread.parser.*;
import org.checkthread.policy.*;

final public class NodeMethod extends AbstractNode {
	private String fFullMethodName;
    private IMethodInfo fMethodInfo;
    
	NodeMethod(String fullFieldName, 
			   String shortName,
			   IMethodInfo methodInfo) 
	{
	    super();
		fFullMethodName = fullFieldName;
		this.setShortName(shortName);
		fMethodInfo = methodInfo;
	}	
	
	public void setMethodInfo(IMethodInfo methodInfo) {
		fMethodInfo = methodInfo;
	}
	
	public IMethodInfo getMethodInfo() {
		return fMethodInfo;
	}
	
	public boolean isEntry() {
		boolean retval;
		if (fMethodInfo == null) {
			retval = true;
		} else {
			IThreadPolicy policy = fMethodInfo.getThreadPolicy();
			if (policy != null) {
				if (policy instanceof ThreadSafePolicy) {
					retval= true;
				} else {
					retval = true;
				}
			} else {
				retval = true;
			}
		}
		return retval;
	}
	
	public String getFullMethodName() {
		return fFullMethodName;
	}
	
	public String getName() {
		return getFullMethodName();
	}

    public INode getLastMethodPredecessor() {
        return this;	
    }
    
}
