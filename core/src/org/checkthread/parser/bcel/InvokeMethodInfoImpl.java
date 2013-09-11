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

package org.checkthread.parser.bcel;

import java.lang.reflect.*;

import org.checkthread.parser.*;
import org.checkthread.policy.*;
import org.checkthread.config.Log;
import org.checkthread.main.Constants;

/**
 * Value object container holding information
 * about a method invocation.
 */
final public class InvokeMethodInfoImpl implements IInvokeMethodInfo  {
    
    private IMethodInfo fParentMethodInfo;
    private String fInvokedMethodName;
    private String fInvokedFullMethodName;
    private IThreadPolicy fInvokedThreadPolicy;
    private String fInvokedFieldName;
    
    private int fInvokeLineNumber = Constants.NO_LINE_NUMBER;
    private boolean fIsSynchronized;
    private boolean fIsInvokedMethodStatic;
    private boolean fIsInvokedMethodOnThis;
    public boolean fIsInvokedOnNonStaticField;
    public boolean fIsInvokedOnStaticField;
    public AccessibleObject fObj;
    
    public InvokeMethodInfoImpl(
            IMethodInfo parent,
            String invokedMethodName,
            String invokedFullMethodName,
            IThreadPolicy invokedThreadPolicy,
            int invokeLineNumber,
            boolean isSynchronized,
            String invokedFieldName,
            boolean isInvokedOnNonStaticField,
            boolean isInvokedOnStaticField,
            boolean isInvokedMethodStatic,
            boolean isInvokedMethodOnThis,
            AccessibleObject obj) 
    {
    	fObj = obj;
    	fIsInvokedOnNonStaticField = isInvokedOnNonStaticField;
    	fIsInvokedOnStaticField = isInvokedOnStaticField;
    	fInvokedFullMethodName = invokedFullMethodName;
    	fIsInvokedMethodOnThis = isInvokedMethodOnThis;
    	fIsInvokedMethodStatic = isInvokedMethodStatic;
    	fInvokedFieldName = invokedFieldName;
        fParentMethodInfo = parent;
        fInvokeLineNumber = invokeLineNumber;
        fIsSynchronized = isSynchronized;
        fInvokedMethodName = invokedMethodName;
        fInvokedThreadPolicy = invokedThreadPolicy;
        
        Log.logByteInfo("InvokeMethodInfoImpl");
        Log.logByteInfo("Parent Method: " + parent.getMethodName());
        if(parent.getThreadPolicy()!=null) {
        	Log.logByteInfo("Parent thread policy: " + parent.getThreadPolicy().getThreadConfinedName());
        }
        Log.logByteInfo("Invoked Method: " + invokedMethodName);
        if(invokedThreadPolicy!=null) {
        	Log.logByteInfo("Invoked thread policy: " + invokedThreadPolicy.getThreadConfinedName());
        }
        Log.logByteInfo("isSynchronized: " + isSynchronized);
    }
    
    public void setInvokedThreadPolicy(IThreadPolicy policy) {
       fInvokedThreadPolicy = policy;	
    }
    
    public AccessibleObject getMethod() {
    	return fObj;
    }
    
    public boolean isInvokedMethodStatic() {
       return fIsInvokedMethodStatic;	
    }
    
    public String getInvokedFieldName() {
    	return fInvokedFieldName;
    }

    public String getInvokedFullMethodName() {
    	return fInvokedFullMethodName;
    }

    public boolean isInvokedMethodOnThis() {
    	return fIsInvokedMethodOnThis;
    }
    
    public boolean isInvokedMethodOnNonStaticField() {
    	return this.fIsInvokedOnNonStaticField;
    }
    
    public boolean isInvokedMethodOnStaticField() {
    	return this.fIsInvokedOnStaticField;
    }
    
    public boolean isInvokedMethodOnField() {
    	return fInvokedFieldName!=null;
    }
    
    public boolean isSynchronized() {
    	return fIsSynchronized;
    }
    
    public boolean isStaticBlock() {
        return fParentMethodInfo.isStaticBlock();
    }
    
    public IMethodInfo getParentMethodInfo() {
        return fParentMethodInfo;
    }
    
    public int getLineNumber() {
        return fInvokeLineNumber;
    }
    
    public String getPathToClassFile() {
        return fParentMethodInfo.getPathToClassFile();
    }
    
    public String getSourceFile() {
        return fParentMethodInfo.getSourceFile();
    }
    
    public String getInvokedMethodName() {
    	return fInvokedMethodName;
    }
    
    public IThreadPolicy getInvokedThreadPolicy() {
    	return fInvokedThreadPolicy;
    }
}
