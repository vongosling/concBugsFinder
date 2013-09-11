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

package org.checkthread.policyerrors;

import org.checkthread.config.Log;
import org.checkthread.main.Constants;
import org.checkthread.main.ICheckThreadError;
import org.checkthread.parser.IInvokeMethodInfo;
import org.checkthread.policy.IThreadPolicy;

public class ErrorParentSynchronizationScopeTooSmall implements ICheckThreadError  {
	
	final private IInvokeMethodInfo fInvokeInfo;
	
	public  ErrorParentSynchronizationScopeTooSmall(IInvokeMethodInfo invokeMethodInfo)
	{
		fInvokeInfo = invokeMethodInfo;	
    }

	public String getErrorMessage() {
		return "The invoked method '" + getInvokedName()
				+ "' has a synchronization scope greater than the parent method." + 
				" Change the synchronization scope of the parent method" + 
				" or add synchronization when calling " + getInvokedName() + ".";
	}
	
	public void printErr() {
		Log.reportThreadPolicyError("****");
		if (getLineNumber() == Constants.NO_LINE_NUMBER) {
			Log.reportThreadPolicyError("Error in class "
					+ getPathToClassFile());
		} else {
			Log.reportThreadPolicyError("Error in class " + getSourceFile()
					+ " on line " + getLineNumber());
		}
		Log.reportThreadPolicyError(getErrorMessage());
	}
	
	public String getParentName() {
		return fInvokeInfo.getParentMethodInfo().getMethodName();
	}

	public String getInvokedName() {
		return fInvokeInfo.getInvokedMethodName();
	}
	
	public IInvokeMethodInfo getInvoked() {
		return fInvokeInfo;
	}

	public IThreadPolicy getParentThreadPolicy() {
		return fInvokeInfo.getParentMethodInfo().getThreadPolicy();
	}

	public IThreadPolicy getInvokedThreadPolicy() {
		return fInvokeInfo.getInvokedThreadPolicy();
	}

	public int getLineNumber() {
	    return fInvokeInfo.getLineNumber();
	}

	public String getSourceFile() {
		return fInvokeInfo.getSourceFile();
	}

	public String getClassFilePath() {
		return fInvokeInfo.getPathToClassFile();
	}

	public String getPathToClassFile() {
		return fInvokeInfo.getPathToClassFile();
	}
}
