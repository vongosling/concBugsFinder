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

package org.checkthread.parser.bcel;

import org.checkthread.annotations.*;
import org.checkthread.parser.*;
import org.checkthread.policy.*;
import org.checkthread.util.*;

/**
 * Value object holding information about a method
 */
final public class MethodInfoImpl implements IMethodInfo {
	
	private String fMethodName;
	private String fSourceFile;
	private String fPathToClassFile;
	private boolean fIsStaticBlock;
	private boolean fIsStaticMethod;
	private boolean fIsSynthetic;
	private boolean fIsSynchronized;
	private boolean fIsConstructor;
	private int fLineNumber;
	private IThreadPolicy fThreadPolicy;
	private IThreadPolicy fThreadPolicySuperClass;
	private String fFullUniqueMethodName;
	private String fClassName;
	
	public static MethodInfoImpl newInstance(
			String sourceFile,
			String pathToClassFile,
			boolean isStaticBlock,
			Class classReflection,
			java.lang.reflect.AccessibleObject methodReflection,
			org.apache.bcel.classfile.Method methodBCEL,
			int lineNumber) {
		
		IThreadPolicy threadPolicy, threadPolicySuperClass = null;
		String uniqueName = null;
		String className = classReflection.getName();
		boolean isConstructor = false;
		
		if(methodReflection==null && isStaticBlock) {
			boolean isImplicit = true;
		    threadPolicy = new ThreadConfinedPolicy(false,ThreadName.MAIN,isImplicit);
		    uniqueName = ByteCodeConstants.STATICBLOCK_IDENTIFIER;
		} else {
		    threadPolicy = PolicyFactory.getThreadPolicy(className,methodReflection);			    
		    if(methodReflection instanceof java.lang.reflect.Method) {
			    uniqueName = methodReflection.toString();
		    	java.lang.reflect.Method m = (java.lang.reflect.Method)methodReflection;
		        java.lang.reflect.Method superMethod = ReflectUtil.getClosestSuperMethod(classReflection, m);
		        className = m.getDeclaringClass().getName();
		        if(superMethod!=null) {
		        	threadPolicySuperClass = PolicyFactory.getThreadPolicy(className,superMethod);
		        }
		    }	
		   
		    if(methodReflection instanceof java.lang.reflect.Constructor) {
		    	isConstructor = true;
			    uniqueName = methodReflection.toString();
		    	java.lang.reflect.Constructor c = (java.lang.reflect.Constructor)methodReflection;
		        java.lang.reflect.Constructor superConstructor= ReflectUtil.getClosestSuperConstructor(classReflection, c);
		        className = c.getDeclaringClass().getName();
		        if(superConstructor!=null) {
		        	threadPolicySuperClass = PolicyFactory.getThreadPolicy(className,superConstructor);
		        }
		    }
		}		
	
		return new MethodInfoImpl(
				 methodBCEL.getName(),
				 uniqueName ,
				 sourceFile,
				 pathToClassFile,
				 className,
				 isStaticBlock,
				 methodBCEL.isStatic(),
				 methodBCEL.isSynthetic(),
				 methodBCEL.isSynchronized(),
				 isConstructor,
				 lineNumber,
				 threadPolicy,
				 threadPolicySuperClass);
	}
	private MethodInfoImpl(String methodName,
			              String fullUniqueMethodName,
			              String sourceFile,
			              String pathToClassFile,
			              String className,
			              boolean isStaticBlock,
			              boolean isStaticMethod,
			              boolean isSynthetic,
			              boolean isSynchronized,
			              boolean isConstructor,
			              int lineNumber,
			              IThreadPolicy threadPolicy,
			              IThreadPolicy threadPolicySuperClass) {
		fMethodName = methodName;
		fClassName = className;
		fSourceFile = sourceFile;
		fPathToClassFile = pathToClassFile;
		fIsStaticBlock = isStaticBlock;
		fIsStaticMethod = isStaticMethod;
		fIsSynthetic = isSynthetic;
		fIsSynchronized = isSynchronized;
		fIsConstructor = isConstructor;
		fLineNumber = lineNumber;
		fThreadPolicy = threadPolicy;
		fFullUniqueMethodName = fullUniqueMethodName;
		fThreadPolicySuperClass = threadPolicySuperClass;
	}

	
	public boolean isConstructor() {
		return fIsConstructor;
	}
	
	public String getClassName() {
		return fClassName;
	}
	
	public IThreadPolicy getSuperClassThreadPolicy() {
		return fThreadPolicySuperClass;
	}
	
	public boolean isStaticBlock() {
		return fIsStaticBlock;
	}
	
	public boolean isStaticMethod() {
	    return fIsStaticMethod;
	}
	
	public boolean isSynthetic() {
		return fIsSynthetic;
	}

	public boolean isSynchronized() {
		return fIsSynchronized;
	}
	
	public IThreadPolicy getThreadPolicy() {
		return fThreadPolicy;
	}
	
    public int getLineNumber() {
    	return fLineNumber;
    }
    
    public String getPathToClassFile() {
    	return fPathToClassFile;
    }
    public String getSourceFile() {
    	return fSourceFile;
    }
    
	public String getMethodName() {
		return fMethodName;
	}
	
	public String getFullUniqueMethodName() {
		return fFullUniqueMethodName;
	}
}
