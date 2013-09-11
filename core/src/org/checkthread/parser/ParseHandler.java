/*
Copyright (c) 2009 Joe Conti, CheckThread.org

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

package org.checkthread.parser;

import java.util.*;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.policyerrors.*;
import org.checkthread.config.*;
import org.checkthread.policy.*;
import org.checkthread.annotations.*;
import org.checkthread.deadlockdetection.*;

/**
 * Implementation of the parse handler interface used
 * by CheckThread static analysis engine.
 */
public class ParseHandler implements IClassFileParseHandler {
	private ArrayList<ICheckThreadError> fPolicyErrorList = new ArrayList<ICheckThreadError>();

	private HashMap<String, IPutGetFieldInfo> fPutFieldCache = new HashMap<String, IPutGetFieldInfo>();
	private HashMap<String, IPutGetFieldInfo> fGetFieldCache = new HashMap<String, IPutGetFieldInfo>();
	
	private HashMap<String,IInvokeMethodInfo> fNotThreadSafeFieldCache = new HashMap<String,IInvokeMethodInfo>();
	
	private HashMap<String,String> fDerivedClassForFieldName = new HashMap<String,String>();

	public static boolean fFoundThreadPolicy = false;
	
	public ArrayList<ICheckThreadError> getThreadPolicyErrors() {
		return fPolicyErrorList;
	}

	public boolean foundThreadPolicy() {
		return fFoundThreadPolicy;
	}
	
	public void clear() {
		fPolicyErrorList.clear();
		fDerivedClassForFieldName.clear();
	}

	public void handleStartClass(Class cls) {}

	public void handleStopClass(Class cls) {}

	public boolean handleStartMethod(IMethodInfo methodInfo) {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		boolean doInspectMethod = true;
		fGetFieldCache.clear();
		fPutFieldCache.clear();

		// get info
		IThreadPolicy policy = methodInfo.getThreadPolicy();
		ICheckThreadError error = null;
		IThreadPolicy sp = methodInfo.getSuperClassThreadPolicy();
		
		// method DOES NOT have a thread policy
		if(policy==null) {
			
			// if parent method DOES have a thread policy
			if (sp != null) {

				// if the parent thread policy is NOT implicit
				if (!sp.isImplicit()) {
					error = new ErrorAnnotationOverride(methodInfo);
					fPolicyErrorList.add(error);
				}
			}
			
			// if parent method is NOT a static method then we can ignore it
			// we can't ignore static blocks because they will always have
			// no thread policy defined. There is no way to put an annotation
			// on a static block
			//if(!methodInfo.isStaticBlock()) {
			   //doInspectMethod = false;
			//}
			
		// method DOES have a thread policy	
		} else {
			
			// if thread policy is not implicit
			if(!policy.isImplicit()) {
			   configBean.setFoundCheckThreadAnnotations(true);
			} 
			
			fFoundThreadPolicy = true;
			
			// suppress error reporting
			if (policy.isSuppressErrors()) {
				   return doInspectMethod;
			
			} else {
				if(sp!=null && !policy.isEquivalent(sp)) {
					error = new ErrorAnnotationOverride(methodInfo);
					fPolicyErrorList.add(error);
				} 

				LockAdjacencyListManager.handleStartMethod(methodInfo);

			}
		}
		
		if (error == null) {
			// static annotation error check
			if (policy instanceof NotThreadSafePolicy) {
				NotThreadSafePolicy p = (NotThreadSafePolicy) policy;
				if (p.getSyncScope() == Scope.INSTANCE
						&& methodInfo.isStaticMethod()) {
					error = new ErrorInstanceMethodWithStaticThreadNotSafe(
							methodInfo);
					fPolicyErrorList.add(error);
				}
			}
		}
        return doInspectMethod;
	}

    public void handlePushLock(IMethodInfo info, ILockInfo lockInfo) {
    	IThreadPolicy policy = info.getThreadPolicy();
    	if(policy!=null) {
    	     LockAdjacencyListManager.handlePushLock(info,lockInfo);
    	}
    }

    public void handlePopLock(IMethodInfo info) {
    	IThreadPolicy policy = info.getThreadPolicy();
    	if(policy!=null) {
    	     LockAdjacencyListManager.handlePopLock(info);
    	}
    }
    
	public void handleStopMethod(IMethodInfo info) {
		IThreadPolicy policy = info.getThreadPolicy();

		if (policy != null && policy.isSuppressErrors()) {
			return;
		}
		
		if(policy!=null) {
		   LockAdjacencyListManager.handleStopMethod(info);
		}
		
		// check cache for unsynchronized read/write
		Iterator<String> i = fPutFieldCache.keySet().iterator();
		while (i.hasNext()) {
			String fieldName = i.next();
			IPutGetFieldInfo hasPut = fPutFieldCache.get(fieldName);
			IPutGetFieldInfo hasGet = fGetFieldCache.get(fieldName);
			if (hasPut != null && hasGet != null) {
				ICheckThreadError error = new ErrorUnsynchronizedFieldReadWriteInThreadSafeMethod(
						hasPut);
				fPolicyErrorList.add(error);
			}
		}
	}

	public void handleInvokeMethod(IInvokeMethodInfo info) {		
		IThreadPolicy parentPolicy = info.getParentMethodInfo().getThreadPolicy();

		// if invoking from ThreadConfined method
		if (parentPolicy != null
				&& parentPolicy instanceof ThreadConfinedPolicy) {
			
			IThreadPolicy invokedPolicy = info.getInvokedThreadPolicy();

			// if invoking a thread unsafe method
			if (invokedPolicy instanceof NotThreadSafePolicy) {

				// if not synchronized
				if (!info.isSynchronized()) {
					String invokedField = info.getInvokedFieldName();
					if (invokedField != null) {
						Object obj = fNotThreadSafeFieldCache.get(invokedField);

						// if field has already been invoked by a method
						if (obj instanceof IInvokeMethodInfo) {
							IInvokeMethodInfo otherInfo = (IInvokeMethodInfo) obj;

							// if different parent method
							if (!otherInfo.getParentMethodInfo()
									.getFullUniqueMethodName().equals(
											info.getParentMethodInfo()
													.getFullUniqueMethodName())) {

								
								// get parent ThreadConfined Name
								String confinedName = ((ThreadConfinedPolicy)parentPolicy).getThreadConfinedName();
								IMethodInfo otherParentMethodInfo = otherInfo.getParentMethodInfo();
								IThreadPolicy otherThreadConfinedPolicy = otherParentMethodInfo.getThreadPolicy();
								String otherConfinedName = ((ThreadConfinedPolicy)otherThreadConfinedPolicy).getThreadConfinedName();
								
								// if different ThreadConfined names
								if(!confinedName.equals(otherConfinedName)) {
								
								   // throw error
								   ICheckThreadError error = new ErrorThreadConfinedInvokingSharedThreadUnafe(
										info, otherInfo);
								   fPolicyErrorList.add(error);
								}
							}
						} else {
							fNotThreadSafeFieldCache.put(invokedField, info);
						}
					}
				}

			}
		}

		// for synchronized loop detection
		if (parentPolicy != null) {
			LockAdjacencyListManager.handleInvokeMethod(info);
		}

		if (parentPolicy != null && parentPolicy.isSuppressErrors()) {
			return;
		}
		Log.debugInfo("ParseHandler: Invoked Method: "
				+ info.getInvokedMethodName() + " for class "
				+ info.getPathToClassFile() + " at line "
				+ Integer.toString(info.getLineNumber()));
		
		String derivedClass = fDerivedClassForFieldName.get(info.getInvokedFieldName());
		if(derivedClass!=null) {
		   IThreadPolicy policy = PolicyFactory.getDerivedThreadPolicy(derivedClass,info.getMethod());
		   if(policy!=null) {
		       info.setInvokedThreadPolicy(policy);
		   }
		}
		checkThreadPolicy(info);
	}

	// not used
	public void handlePutField(IPutFieldInfo info) {
		if (info.getParentMethodInfo().isConstructor() && info.isFinal()) {
			if (info.getPutFieldClassName()!=null 
					&& !info.getPutFieldClassName().equals(info.getDeclaredFieldClassName())) {
				
				fDerivedClassForFieldName.put(info.getFullFieldName(), info.getPutFieldClassName());
/*
				System.out.println("PUTFIELD");
				System.out.println(info.getFullFieldName());
				System.out.println(info.getDeclaredFieldClassName());
				System.out.println(info.getPutFieldClassName());
*/
			}
		}
    }   
    
    // not used
    public void handleGetField(IGetFieldInfo info) {}
    
	public void handlePutGetField(IPutGetFieldInfo data) {
		
		// When looking for race conditions on shared data,
		// ignore constructors operating on non-static class fields since 
		// this data can not be shared across multiple threads at 
		// construction time.		
		boolean processField = false;
		IMethodInfo parentMethodInfo = data.getParentMethodInfo();
		String className = parentMethodInfo.getClassName();
        if (parentMethodInfo != null && className != null 
        	&& data.getFullFieldName().startsWith(className)
        	&& (!parentMethodInfo.isConstructor() || data.isStaticField())) {
				processField = true;
        }

		if (processField) {
			IThreadPolicy policy = data.getParentMethodInfo()
					.getSuperClassThreadPolicy();
			if (policy != null && policy.isSuppressErrors()) {
				return;
			}
			if (!data.isSynchronized()) {
				policy = data.getParentMethodInfo().getThreadPolicy();
				if (policy instanceof ThreadSafePolicy) {
					// get field
					if (data.getFieldAccess() == IPutGetFieldInfo.FieldAccessEnum.GET) {
						if (fPutFieldCache.get(data.getFullFieldName()) != null) {
							fGetFieldCache.put(data.getFullFieldName(), data);
						}

						// put field
					} else {
						fPutFieldCache.put(data.getFullFieldName(), data);
					}
				}
			}
		}
	}

	private void checkThreadPolicy(IInvokeMethodInfo info) {
		IThreadPolicy policy = info.getParentMethodInfo().getThreadPolicy();

		if (policy != null && policy.isSuppressErrors()) {
			return;
		}
		ICheckThreadError result = PolicyComparer.compareThreadPolicy(info);
		if (result != null) {
			fPolicyErrorList.add(result);
		}
	}
}
