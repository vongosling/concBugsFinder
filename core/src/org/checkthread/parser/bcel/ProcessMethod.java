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

import java.io.*;
import java.util.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.structurals.OperandStack;

import org.checkthread.parser.*;
import org.checkthread.config.*;

/**
 * Static analysis for a method
 */
public class ProcessMethod {

	private static int INVALID_PC = -1;
	
	static void inspectMethod(
			ConstantPool constant_pool,
			JavaClass jClass,
			ArrayList<IClassFileParseHandler> handlerList,
			HashMap<String, java.lang.reflect.AccessibleObject> hashMapSynthetic,
			String className, 
			Method methodBCEL) throws Exception {

		// Get raw signature
		String signature = methodBCEL.getSignature();

		// Get array of strings containing the argument types
		String[] args = Utility.methodSignatureArgumentTypes(signature, false);

		// Get method name
		String name = methodBCEL.getName();

		boolean isStaticBlock = false;

		java.lang.reflect.AccessibleObject methodReflection;
		String parentMethodName = null;
		if (name.equals(ByteCodeConstants.STATICBLOCK_IDENTIFIER)) {
			isStaticBlock = true;
		}

		parentMethodName = Util.cleanMethod(name);
		methodReflection = Util.loadMethod(className, parentMethodName, args,
				methodBCEL.isSynthetic());

		Code code = methodBCEL.getCode();
		inspectCode(constant_pool, jClass, handlerList, hashMapSynthetic,
				isStaticBlock, methodBCEL.isSynthetic(), methodBCEL, methodReflection, code);
	}

	private static void inspectCode(
			ConstantPool constant_pool,
			JavaClass jClass,
			ArrayList<IClassFileParseHandler> handlerList,
			HashMap<String, java.lang.reflect.AccessibleObject> hashMapSynthetic,
			boolean isStaticBlock, 
			boolean isParentSynthetic,
			Method methodBCEL,
			java.lang.reflect.AccessibleObject methodReflection, 
			Code code) throws Exception {


		if (code != null) {

			LineNumberTable lineTable = code.getLineNumberTable();

			String pkg = jClass.getPackageName();
			String sourceFile = null;
			if (pkg != null) {
				sourceFile = jClass.getPackageName().replace(".", File.separator)
						+ File.separator + jClass.getSourceFileName();
			} else {
				sourceFile = jClass.getSourceFileName();
			}
			int lineNumber = lineTable.getSourceLine(0);
                      
			Class classReflection = ClassLoaderBridge.loadClass(jClass.getClassName());
			
			IMethodInfo methodInfo = MethodInfoImpl.newInstance(
		              sourceFile,
		              jClass.getFileName(),
		              isStaticBlock,
		              classReflection,
		              methodReflection,
		              methodBCEL,
		              lineNumber); 
      
			// notify handlers
			boolean doInspectMethod = true;
			if (handlerList != null
					&& (methodReflection instanceof java.lang.reflect.Method 
							|| methodReflection instanceof java.lang.reflect.Constructor)) {
				
				for (IClassFileParseHandler handler : handlerList) {
					if(!handler.handleStartMethod(methodInfo)) {
						doInspectMethod = false;
						break;
					}
				}
			}

			// the following performance optimization did NOT speed things
			// up when analyzing a large jar file with no checkthread
			// annotations (I used rt.jar). Not sure why, I would
			// expect it to speed things up since we are not stepping
			// through byte code
			if(!doInspectMethod) {
				return;
		    }
			
			CodeException[] exceptionTable = code.getExceptionTable();
			int[] exBranchPoint = null;
			int[] exHandlerPoint = null;
			
			if (exceptionTable.length > 0) {
				exBranchPoint = new int[exceptionTable.length];
				exHandlerPoint = new int[exceptionTable.length];
				for (int n = 0; n < exceptionTable.length; n++) {
					exBranchPoint[n] = exceptionTable[n].getStartPC();
					exHandlerPoint[n] = exceptionTable[n].getHandlerPC();
				}
			}
			
			// global references per code block
			Queue<BranchState> branchQueue = new LinkedList<BranchState>();			
			HashMap<Integer, Type> runtimeVariableTable = new HashMap<Integer, Type>();
            HashMap<Integer,Boolean> visitedByteOffsets = new HashMap<Integer,Boolean>();
            
            // create initial branch
			OperandStack stack = new OperandStack(code.getMaxStack());
			LockStack lockInfo = new LockStack(methodBCEL.isSynchronized());
			BranchState currBranch = new BranchState(stack,lockInfo,0);
            
			// add branch to queue
			branchQueue.add(currBranch);
            
			// loop through branches
			// this invoked code will add new branches to the queue
			// therefore, this loop can repeat many times for code
			// that contains control flow (e.g. if/while/for/try)
            while(branchQueue.peek()!=null) {
            	currBranch = branchQueue.poll();          
			    inspectCodeRecurse(exBranchPoint, exHandlerPoint, visitedByteOffsets,branchQueue, currBranch, runtimeVariableTable, jClass,
					handlerList, hashMapSynthetic, methodBCEL, methodReflection,
					lockInfo,methodInfo);
            }
            
			// notify handlers
			if (handlerList != null
					&& (methodReflection instanceof java.lang.reflect.Method 
							|| methodReflection instanceof java.lang.reflect.Constructor)) {
				for (IClassFileParseHandler handler : handlerList) {
					handler.handleStopMethod(methodInfo);
				}
			}
		}
	}

	private static void inspectCodeRecurse(
			int[] exBranchPoint,
			int[] exHandlerPoint,
			HashMap<Integer,Boolean> processedByteOffset,
			Queue<BranchState> branchQueue,
			BranchState currBranch,
			HashMap<Integer, Type> runtimeVariableTable,
			JavaClass jClass,
			ArrayList<IClassFileParseHandler> handlerList,
			HashMap<String, java.lang.reflect.AccessibleObject> hashMapSynthetic,
			Method parentMethod,
			java.lang.reflect.AccessibleObject parentMethodR, 
			LockStack lockInfo,
			IMethodInfo parentMethodInfo)
			throws Exception 
	{
		Code code = parentMethod.getCode();
			
		// create new byte reader
		ByteReader bytes = new ByteReader(code.getCode());
		bytes.mark(bytes.available());
		bytes.reset();
		
		// offset byte reader for the specified branch index
        bytes.skipBytes(currBranch.getIndex());
        
        Log.logByteInfo("^^^ RUNING BRANCH" + bytes.getIndex());
    	
		// process method
		for (int i = 0; bytes.available() > 0; i++) {

			// Add a new branch if this is a branch point identified in 
			// the exception table
			int pc = bytes.getIndex();
			if (exBranchPoint != null) {
				for (int n = 0; n < exBranchPoint.length; n++) {
					if (exBranchPoint[n] == pc) {

						// create new branch point
						OperandStack branchStack = currBranch.getStack()
								.getClone();
						LockStack branchLockInfo = currBranch.getLockInfo().getClone();
						BranchState branchState = new BranchState(branchStack,
								branchLockInfo, exHandlerPoint[n]);
						// add branch to queue
						branchQueue.add(branchState);
						
						// remove
						exBranchPoint[n] = INVALID_PC;
					}
				}
			}
			
			// System.out.println("Byte offset: " + bytes.getIndex());
			short opcode = (short) bytes.readUnsignedByte();
			Object obj = processedByteOffset.get(bytes.getIndex());
			if (obj != null && ((Boolean) obj == true)) {
				return;
			} else {
				processedByteOffset.put(bytes.getIndex(), true);
				ProcessInstruction.processInstruction(opcode,
						processedByteOffset, branchQueue, currBranch,
						runtimeVariableTable, jClass, handlerList,
						hashMapSynthetic, parentMethod, parentMethodR, bytes,
						parentMethodInfo);
			}
		}
	}
}
