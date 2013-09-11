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
import java.util.HashMap;

import org.apache.bcel.classfile.*;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.apache.bcel.generic.*;
import org.checkthread.config.Log;
import org.checkthread.parser.IMethodInfo;

/**
 * Misc. utilities for processing byte code instructions
 */
final public class ProcessInstruction_Util {

	static void logInfo(String msg) {
		Log.logByteInfo(msg);
	}

	static int getVariableIndex(boolean wide, ByteReader bytes)
			throws IOException {
		int vindex;
		if (wide) {
			vindex = bytes.readShort();
			wide = false; // Clear flag
		} else {
			vindex = bytes.readUnsignedByte();
		}
		return vindex;
	}

	static int getIndexIntIcecream(ByteReader bytes) throws IOException {
		return bytes.getIndex() + bytes.readInt() - 1;
	}

	static int getIndexShortIcecream(ByteReader bytes) throws IOException {
		return (int) (bytes.getIndex() + bytes.readShort() - 1);
	}
	
	// ALOAD - Load reference from local variable
    // Stack: ... -> ..., objectref
	static boolean processALOAD(int vindex, OperandStack stack, 
			boolean wide,
			ByteReader bytes, 
			LocalVariableTable varTable,
			HashMap<Integer,Type> runtimeVariableTable) throws IOException {
		
		logInfo("ALOAD_" + vindex + ", stack size: " + stack.size());
		LocalVariable localVariable = null;
		if(varTable!=null) {
			localVariable = varTable.getLocalVariable(vindex,0);
		}
		String fullClassName= "DUMMY";
		String varName = null;
		if(localVariable!=null) {
			
			varName = localVariable.getName();
			fullClassName = Utility.signatureToString(localVariable.getSignature());

		} else {
			Type t = runtimeVariableTable.get(vindex);
			if(t instanceof ObjectType) {
				ObjectType ot = (ObjectType)t;
				fullClassName = ot.getClassName();			
			}
		}

		// push reference to stack
		if (varName != null && varName.equals("this")) {
			ThisReferenceType type = new ThisReferenceType(fullClassName);
			stack.push(type);
		} else {
			ObjectType type = new ObjectType(fullClassName);
			stack.push(type);
		}

		return false; // clear flag
	}
	static boolean processALOADn(OperandStack stack, 
			boolean wide,
			ByteReader bytes, 
			LocalVariableTable varTable,
			HashMap<Integer,Type> runtimeVariableTable) throws IOException {
		
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processALOAD(vindex,stack,wide,bytes,varTable,runtimeVariableTable);
	}
	
	// ASTORE - Store reference into local variable
    // Stack ..., objectref -> .. 
	static boolean processASTORE(int vindex, 
			OperandStack stack, boolean wide,
			ByteReader bytes,
			HashMap<Integer,Type> runtimeVariableTable)
	throws IOException {
        logInfo("ASTORE, stack size: " + stack.size() + " ind:" + vindex);
		wide = false; // clear flag
		if(!stack.isEmpty()) {
		    Type t = stack.pop();
		    logInfo("ASTORE: " + t.getSignature());
		    runtimeVariableTable.put(vindex, t);
		}
		return false; // clear flag
	}
	
	static boolean processASTOREn(OperandStack stack, boolean wide,
			ByteReader bytes,HashMap<Integer,Type> runtimeVariableTable) throws IOException {
		
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processASTORE(vindex,stack,wide,bytes,runtimeVariableTable);
	}
	
	static boolean processDLOAD(int vindex, OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		
		logInfo("DLOAD_" + vindex + ", stack size: " + stack.size());

		// push to stack
		BasicType type = Type.DOUBLE;
		stack.push(type);

		return false; // clear flag
	}
	
	static boolean processDLOADn(OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processDLOAD(vindex,stack,wide,bytes,varTable);
	}
		
	// DSTORE - Store double into local variable
	// Stack: ..., value.word1, value.word2 -> ...
	static boolean processDSTORE(int index, OperandStack stack, boolean wide, ByteReader bytes)
	throws IOException
	{
			logInfo("DSTORE_"+Integer.toString(index));
			if(!stack.isEmpty()) {
				stack.pop();
			}
			return false; // clear flag
	}	
	static boolean processDSTOREn(OperandStack stack, boolean wide,
			ByteReader bytes) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processDSTORE(vindex,stack,wide,bytes);
	}
	
		
    // FLOAD - Load float from local variable
    // Stack ... -> ..., result	
	static boolean processFLOAD(int vindex, OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		
		logInfo("FLOAD_" + vindex + ", stack size: " + stack.size());
					
		// push to stack		
		BasicType type = Type.FLOAT;		
		stack.push(type);

		return false; // clear flag
	}
	static boolean processFLOADn(OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processFLOAD(vindex,stack,wide,bytes,varTable);
	}
	
	// FSTORE - Store float into local variable
	// Stack: ..., value -> ... 
	static boolean processFSTORE(int index, OperandStack stack, boolean wide, ByteReader bytes)
	throws IOException
	{
			logInfo("FSTORE_"+Integer.toString(index));
			if(!stack.isEmpty()) {
				stack.pop();
			}
			return false; // clear flag
	}	
	static boolean processFSTOREn(OperandStack stack, boolean wide,
			ByteReader bytes) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processFSTORE(vindex,stack,wide,bytes);
	}
	
	// ILOAD - Load int from local variable onto stack
	// Stack: ... -> ..., result
	static boolean processILOAD(IMethodInfo parentMethodInfo,
			int vindex, 
			OperandStack stack, 
			boolean wide,
			ByteReader bytes, 
			LocalVariableTable varTable) throws IOException {
		
		logInfo("ILOAD_" + vindex + ", stack size: " + stack.size());

		// push to stack
		BasicType type = Type.INT;
		if(stack.size()>=stack.maxStack()) {
			Log.severe("ERROR: max stack exceeded in " + parentMethodInfo.getFullUniqueMethodName());;
		}
		stack.push(type);

		return false; // clear flag
	}
	static boolean processILOADn(IMethodInfo parentMethodInfo,
			OperandStack stack, 
			boolean wide,
			ByteReader bytes, 
			LocalVariableTable varTable) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processILOAD(parentMethodInfo,vindex,stack,wide,bytes,varTable);
	}
	
	// ISTORE - Store int from stack into local variable
	// Stack: ..., value -> ... 
	static boolean processISTORE(int index, OperandStack stack, boolean wide, ByteReader bytes)
	throws IOException
	{
			logInfo("ISTORE_"+Integer.toString(index) + ", stack size: " + stack.size());
			if(!stack.isEmpty()) {
				stack.pop();
			}
			return false; // clear flag
	}	
	static boolean processISTOREn(OperandStack stack, boolean wide,
			ByteReader bytes) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processISTORE(vindex,stack,wide,bytes);
	}
	
	// LLOAD - Load long from local variable
	// Stack ... -> ..., result.word1, result.word
	static boolean processLLOAD(int vindex, OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		
		logInfo("LLOAD_" + vindex + ", stack size: " + stack.size());

		// push to stack
		BasicType type = Type.LONG;
		stack.push(type);

		return false; // clear flag
	}
	static boolean processLLOADn(OperandStack stack, boolean wide,
			ByteReader bytes, LocalVariableTable varTable) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processLLOAD(vindex,stack,wide,bytes,varTable);
	}
	
	// LSTORE - Store long into local variable
	// Stack: ..., value.word1, value.word2 -> ... 
	static boolean processLSTORE(int index, OperandStack stack, boolean wide, ByteReader bytes)
	throws IOException
	{
			logInfo("LSTORE_"+Integer.toString(index));
			if(!stack.isEmpty()) {
				stack.pop();
			}
			return false; // clear flag
	}	
	static boolean processLSTOREn(OperandStack stack, boolean wide,
			ByteReader bytes) throws IOException {
		int vindex = ProcessInstruction_Util.getVariableIndex(wide, bytes);
		return processLSTORE(vindex,stack,wide,bytes);
	}
	
	
}
