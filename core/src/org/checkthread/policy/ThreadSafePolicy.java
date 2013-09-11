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

import org.checkthread.main.ICheckThreadError;
import org.checkthread.parser.IInvokeMethodInfo;
import org.checkthread.policyerrors.ErrorSynchronizationRequired;
import org.checkthread.policyerrors.ErrorThreadConfined;
import org.checkthread.annotations.*;

public class ThreadSafePolicy implements IThreadPolicy {

	private boolean fIsSuppressErrors;
	
	public ThreadSafePolicy(ThreadSafe annotation) {
		fIsSuppressErrors = annotation.suppressErrors();
	}
	
	public ThreadSafePolicy(boolean suppressErrors) {
		fIsSuppressErrors = suppressErrors;
	}

	public boolean isSuppressErrors() {
		return fIsSuppressErrors;
	}
	
	public boolean isImplicit() {
		return false;
	}
	
	public boolean isEquivalent(IThreadPolicy policy) {
		return (policy instanceof ThreadSafePolicy);
	}
	
	public ICheckThreadError getError(IThreadPolicy invokedPolicy,
			IInvokeMethodInfo invokeInfo) {

		ICheckThreadError retval = null;

		// @ThreadSafe invoking @ThreadSafe
		if (invokedPolicy instanceof ThreadSafePolicy) {
			// OKAY 
		}

		// @ThreadSafe invoking @ThreadConfined
		else if (invokedPolicy instanceof ThreadConfinedPolicy) {
			
			// ERROR
			retval = new ErrorThreadConfined(invokeInfo);

		
		// @ThreadSafe invoking @ThreadUnsafe
		} else if (invokedPolicy instanceof NotThreadSafePolicy) {
			
			// OKAY
			if(invokeInfo.isSynchronized()) {

			// OKAY if parent method is constructor and field is not shared
			} else if(invokeInfo.getParentMethodInfo().isConstructor() 
					&& invokeInfo.isInvokedMethodOnNonStaticField()) {
				
			// ERROR
			} else {
				// ERROR if invoking method on shared data (field, this, static class)
				if (invokeInfo.isInvokedMethodOnNonStaticField()
					||	invokeInfo.isInvokedMethodOnStaticField() 
			        || invokeInfo.isInvokedMethodStatic() 
			        || invokeInfo.isInvokedMethodOnThis()) 
				{
					retval = new ErrorSynchronizationRequired(invokeInfo);
				} 
			}
		}
		
		return retval;
	}
	
    public boolean isCompliant(IThreadPolicy invokedpolicy) {

       boolean iscompliant = true;
       if (!(invokedpolicy instanceof ThreadSafePolicy)) {
           iscompliant = false;
       }
       
       return iscompliant;
   }
   
   public String getThreadConfinedName() {
	   return "thread safe";
   }
}
