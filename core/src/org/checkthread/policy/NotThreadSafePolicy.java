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
import org.checkthread.policyerrors.ErrorParentSynchronizationScopeTooSmall;
import org.checkthread.policyerrors.ErrorThreadConfined;
import org.checkthread.annotations.*;

public class NotThreadSafePolicy implements IThreadPolicy {

	private Scope fSyncScope;
	private boolean fIsSuppressErrors;
	
    public NotThreadSafePolicy(NotThreadSafe annotation) {
        fSyncScope = annotation.synchronize();
        fIsSuppressErrors = annotation.suppressErrors();
    }
    
    public NotThreadSafePolicy(Scope scope, boolean isSuppressErrors) {
        fSyncScope = scope;	
        fIsSuppressErrors = isSuppressErrors;
    }
    
	public boolean isImplicit() {
		return false;
	}
	
	public boolean isSuppressErrors() {
		return fIsSuppressErrors;
	}
	
    public Scope getSyncScope() {
    	return fSyncScope;
    }
	
	public boolean isEquivalent(IThreadPolicy policy) {
	    return (policy instanceof NotThreadSafePolicy) 
		   && ((NotThreadSafePolicy)policy).getSyncScope().equals(getSyncScope());
	}
	
	public ICheckThreadError getError(IThreadPolicy invokedPolicy,
			IInvokeMethodInfo invokeInfo) {

		ICheckThreadError retval = null;

		// @NotThreadSafe invoking @ThreadSafe
		if (invokedPolicy instanceof ThreadSafePolicy) {
			// OKAY 
		}

		// @NotThreadSafe invoking @ThreadConfined
		else if (invokedPolicy instanceof ThreadConfinedPolicy) {
			
			// ERROR
			retval = new ErrorThreadConfined(invokeInfo);

		
		// @NotThreadSafe invoking @NotThreadSafe
		} else if (invokedPolicy instanceof NotThreadSafePolicy) {
			NotThreadSafePolicy i = (NotThreadSafePolicy) invokedPolicy;
			
			// if invoked method is synchronized
			if (invokeInfo.isSynchronized()) {
				// No-op OKAY

			// invoked method is not synchronzied
			} else {
				if (getSyncScope() == Scope.INSTANCE
						&& (i.getSyncScope() == Scope.CLASS)) {

					// error
					retval = new ErrorParentSynchronizationScopeTooSmall(
							invokeInfo);
				}
			}
		}
		
		return retval;
	}
   
   public String getThreadConfinedName() {
	   return "thread unsafe";
   }
}
