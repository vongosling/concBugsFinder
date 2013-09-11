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

package org.checkthread.parser.bcel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.verifier.structurals.*;
import org.apache.bcel.generic.*;

import org.checkthread.config.Log;
import org.checkthread.parser.*;
import org.checkthread.policy.*;
import org.checkthread.util.*;

/**
 * Utility class for handling method byte code instructions.
 */
final public class ProcessInstruction_MethodInvoke {
	
	static void processMethodInvoke(
			OperandStack stack,
			ConstantPool constant_pool,
			JavaClass jClass,
			ArrayList<IClassFileParseHandler> handlerList,
			HashMap<String, java.lang.reflect.AccessibleObject> hashMapSynthetic,
			short opcode, 
			ByteReader bytes, 
			int invokedLineNumber, 
			IMethodInfo parentMethodInfo,
			LockStack lockStack) throws Exception 
			{
		int index, class_index, vindex, constant;
		String className;
		String signature;
		Type type;
		String str;
        String methodName;
        String invokedMethodOnFieldName = null;
        int invokeLineNumber;  
        int nargs;
		int line_number_ind = bytes.getIndex();
		boolean isInvokedMethodStatic = false;
		boolean isInvokedMethodOnThis = false;
        boolean isInvokedOnNonStaticField = false;
        boolean isInvokedOnStaticField = false;
        
		// read table index
		int m_index = bytes.readShort();
	
		if (opcode == Constants.INVOKEINTERFACE) {
			bytes.readUnsignedByte(); // Redundant
			bytes.readUnsignedByte(); // Reserved
			ConstantInterfaceMethodref c = (ConstantInterfaceMethodref) constant_pool
					.getConstant(m_index, Constants.CONSTANT_InterfaceMethodref);
			class_index = c.getClassIndex();
			str = constant_pool.constantToString(c);
			index = c.getNameAndTypeIndex();
		} else {
			ConstantMethodref c = (ConstantMethodref) constant_pool.getConstant(m_index,
					Constants.CONSTANT_Methodref);
			class_index = c.getClassIndex();
			str = constant_pool.constantToString(c);
			index = c.getNameAndTypeIndex();
		}
		
		// Get method and class name
		methodName = constant_pool.constantToString(constant_pool
				.getConstant(index, Constants.CONSTANT_NameAndType));
		className = referenceClass(constant_pool, class_index);
		
		// Get signature, i.e., types
		ConstantNameAndType c2 = (ConstantNameAndType) constant_pool
				.getConstant(index, Constants.CONSTANT_NameAndType);
		signature = constant_pool.constantToString(c2.getSignatureIndex(),
				Constants.CONSTANT_Utf8);
		//System.out.println("Sig: " + signature); //(Ljava/lang/String;)V
		String[] args = Utility.methodSignatureArgumentTypes(signature, false);
		nargs = args.length;

		type = null;
		
		switch (opcode) {

		// INVOKESPECIAL - Invoke instance method; special handling for
		// superclass, private and instance initialization method invocations
		// Stack: ..., objectref, [arg1, [arg2 ...]] -> ...
		case Constants.INVOKESPECIAL:
			stack.pop(nargs);
            type = stack.pop();
            Log.logByteInfo("INVOKESPECIAL,  pop nargs: " + nargs);
			break;

		// INVOKEVIRTUAL - Invoke instance method; dispatch based on class
		// Stack: ..., objectref, [arg1, [arg2 ...]] -> ...
		case Constants.INVOKEVIRTUAL:
			stack.pop(nargs);
			type = stack.pop();
            Log.logByteInfo("INVOKEVIRTUAL,  pop nargs: " + nargs + " type: " + type);  
            break;
            
			// INVOKESTATIC - Invoke a class (static) method
			// Stack: ..., [arg1, [arg2 ...]] -> ...
		case Constants.INVOKESTATIC:
			isInvokedMethodStatic = true;
			Log.logByteInfo("INVOKESTATIC, pop nargs: " + nargs);
            stack.pop(nargs);
            break;

			// INVOKEINTERFACE - Invoke interface method
			// Stack: ..., objectref, [arg1, [arg2 ...]] -> ...
		case Constants.INVOKEINTERFACE:
			stack.pop(nargs);
			type = stack.pop();
			Log.logByteInfo("INVOKEINTERFACE on " + type.getSignature() + " pop nargs: " + nargs);
			break;
		}
			
        if(type instanceof FieldReferenceType) {
        	FieldReferenceType frt = (FieldReferenceType)type;
        	invokedMethodOnFieldName = frt.getFullFieldName();
        	if(frt.isStaticField()) {
                isInvokedOnStaticField = true;
                isInvokedOnNonStaticField = false;
        	} else {
        		isInvokedOnStaticField = false;
                isInvokedOnNonStaticField = true;      		
        	}
        } else if(type instanceof ThisReferenceType) {
        	isInvokedMethodOnThis = true;
        }
        
		if (methodName == null || methodName.length() < 1) {
			throw new Exception("Invalid Method");
		} else {
			// package up information as a data bean

			// this.getClass().get
			java.lang.reflect.AccessibleObject invokedMethodReflection = Util.loadMethod(
					className, methodName, args, parentMethodInfo.isSynthetic());
			
			if(invokedMethodReflection==null) {
				return;
				/*
				System.out.println("Parent method: " + parentMethodInfo.getFullUniqueMethodName());
				System.out.println("className: " + className + " methodName: " + methodName 
						+ " args: " + args + " issyn:" + parentMethodInfo.isSynthetic());
				
				for(String str2 : args) {
					System.out.println("arg: " + str2);
				}
				*/
			}
			String invokedFullMethodName = invokedMethodReflection.toString();
			
			Log.logByteInfo("INVOKE: " + ReflectUtil.getName(invokedMethodReflection));
			
			// Exit early if not a method or constructor
			if (!(invokedMethodReflection instanceof java.lang.reflect.Method)
					&& !(invokedMethodReflection instanceof java.lang.reflect.Constructor)) {

				// To Do: No code path should come through here
			}

			// If method is invoking Lock.lock() or Lock.unlock()
			if (invokedMethodReflection instanceof java.lang.reflect.Method) {
				java.lang.reflect.Method invokedMethod = (java.lang.reflect.Method) invokedMethodReflection;
				//System.out.println("Invoked: " + invokedMethod.getName() + " " + invokedMethod.isSynthetic());
				Class lockClass = java.util.concurrent.locks.Lock.class;
				Class invokedClass = invokedMethod.getDeclaringClass();
				if (lockClass.isAssignableFrom(invokedClass)) {
					if (invokedMethod.getName().equals("lock")) {
						lockStack.push(ObjectType.UNKNOWN);
					} else if (invokedMethod.getName().equals("unlock")) {
						lockStack.pop();
					}
				}

			}

			// Handle output arguments
			String outputArgs = Utility.methodSignatureReturnType(signature,false);
			Log.logByteInfo("Output Args: " + outputArgs);
			if(stack.size() < stack.maxStack() 
					&& outputArgs!=null 
					&& !outputArgs.equals("void")) {
				
				// vm spec, push output to stack
				Log.logByteInfo("push object onto stack");
				stack.push(ObjectType.UNKNOWN);
			}
			
			// If invoked method is a synthetic method, then get the underlying
			// method

			if (isSynthetic(invokedMethodReflection)) {
				String key = invokedMethodReflection.toString();
				invokedMethodReflection = hashMapSynthetic.get(key);
				if (invokedMethodReflection == null) {
					//throw new Exception("Could not find method in hash: " + key);
				}
			}



			// System.out.println("BcelInspectClass: invoked: " + invoked);

			// If parent method is a synthetic method, then store this
			// underlying
			// method
			if (parentMethodInfo.isSynthetic()) {
				// System.out.println("PUT: " + parentMethodR + " " + mInvoked);
				if (parentMethodInfo.getMethodName() != null) {
					String key =  parentMethodInfo.getFullUniqueMethodName();
					if(key==null) {
						//throw new RuntimeException("NULL key");
					}
					hashMapSynthetic.put(parentMethodInfo.getFullUniqueMethodName(), invokedMethodReflection);
				}
			} else {

				String pkg = jClass.getPackageName();
				String srcFile;
				if (pkg != null) {
					srcFile = jClass.getPackageName().replace(".",
							File.separator)
							+ File.separator + jClass.getSourceFileName();
				} else {
					srcFile = jClass.getSourceFileName();
				}
				
				boolean isSynchronized = lockStack.isSynchronized();				

				IThreadPolicy invokedThreadPolicy = PolicyFactory.getThreadPolicy(className,invokedMethodReflection);
				
				String name;
				if(invokedMethodReflection==null) {
					name = null;
				} else {
					name = ReflectUtil.getName(invokedMethodReflection);
				}

				//System.out.println("&&& " + invokedFullMethodName + " " + type.getSignature());
				InvokeMethodInfoImpl invokeInfo = new InvokeMethodInfoImpl(
			            parentMethodInfo,
			            name,
			            invokedFullMethodName,
			            invokedThreadPolicy,
			            invokedLineNumber,
			            isSynchronized,
			            invokedMethodOnFieldName,
			            isInvokedOnNonStaticField,
			            isInvokedOnStaticField,
			            isInvokedMethodStatic,
			            isInvokedMethodOnThis,
			            invokedMethodReflection);

				// pass bean to handlers
				if (handlerList != null) {
					for (IClassFileParseHandler handler : handlerList) {
						handler.handleInvokeMethod(invokeInfo);
					}
				}
			}
		}
	}

	/**
	 * Utility method that converts a class reference in the constant pool,
	 * i.e., an index to a string.
	 */
	private static String referenceClass(ConstantPool constant_pool, int index) {
		String str = constant_pool.getConstantString(index,
				Constants.CONSTANT_Class);
		str = Utility.compactClassName(str);
		String class_package = "BARF";
		str = Utility.compactClassName(str, class_package + ".", true);
		return str;
	}

	private static boolean isSynthetic(java.lang.reflect.AccessibleObject obj) {
		boolean retval = false;
		if (obj instanceof java.lang.reflect.Method) {
			java.lang.reflect.Method m = (java.lang.reflect.Method) obj;
			retval = m.isSynthetic();
		}
		if (retval == false && obj instanceof java.lang.reflect.Constructor) {
			java.lang.reflect.Constructor m = (java.lang.reflect.Constructor) obj;
			retval = m.isSynthetic();
		}
		return retval;
	}
}
