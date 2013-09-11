/*
Copyright (c) 2009 Joe Conti

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

import org.checkthread.policyerrors.*;
import org.checkthread.main.*;
import org.checkthread.parser.*;
import org.checkthread.annotations.*;

public class ThreadConfinedPolicy implements IThreadPolicy {

	private String fLabelID;
	private boolean fIsSuppressErrors;
	private boolean fIsImplicit;

	/**
	 * Called if method is static (hence no annotation)
	 * @param id
	 */
	public ThreadConfinedPolicy(boolean isSuppressErrors, 
			String id,
			boolean isImplicit) {
		super();
		fLabelID = id;
		fIsSuppressErrors = isSuppressErrors;
		fIsImplicit = isImplicit;
	}
	
	public ThreadConfinedPolicy(ThreadConfined annotation, boolean isImplicit) {
		super();
		fLabelID = annotation.value();
		fIsSuppressErrors = annotation.suppressErrors();
		fIsImplicit = isImplicit;
	}

	public boolean isImplicit() {
		return fIsImplicit;
	}
	
	public boolean isSuppressErrors() {
		return fIsSuppressErrors;
	}
	
	public String getLabelID() {
		return fLabelID;
	}

	public String getThreadConfinedName() {
		return fLabelID;
	}
	
	public boolean isEquivalent(IThreadPolicy policy) {
	   return (policy instanceof ThreadConfinedPolicy) 
	   && ((ThreadConfinedPolicy)policy).getThreadConfinedName().equals(this.getThreadConfinedName());
	}
	
	public ICheckThreadError getError(IThreadPolicy invokedPolicy,
			IInvokeMethodInfo invokeInfo) {
		ICheckThreadError retval = null;

		// @ThreadConfined invoking @ThreadSafePolicy
		if (invokedPolicy instanceof ThreadSafePolicy) {
			//OKAY, no error
		}

		// @ThreadConfined invoking @ThreadConfined
		else if (invokedPolicy instanceof ThreadConfinedPolicy) {
			ThreadConfinedPolicy invoked = (ThreadConfinedPolicy) invokedPolicy;
			
			// OKAY if name is the same
			if (invoked.getLabelID().equals(fLabelID)) {

		    // ERROR
			} else {
				retval = new ErrorThreadConfined(invokeInfo);
			}
	    
		// @ThreadConfined invoking @ThreadUnsafe
		} else if(invokedPolicy instanceof NotThreadSafePolicy) {
			
			// invoked method is synchronized
			if(invokeInfo.isSynchronized()) {
			   // OKAY
				
			// invoked method is not synchronized
			} else {
				
				// ERROR if invoking method on global shared data (static class)
				if (invokeInfo.isInvokedMethodStatic()) 
				{
					retval = new ErrorSynchronizationRequired(invokeInfo);
				} 
				//else if (invokeInfo.isInvokedMethodOnField()) {
					// if field is public
					//ToDo: throw error					
				//}
			}	
		}

		return retval;
	}

}

