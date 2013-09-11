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

import java.io.IOException;
import java.util.ArrayList;

import org.apache.bcel.classfile.Utility;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.checkthread.config.Log;
import org.checkthread.parser.IClassFileParseHandler;
import org.checkthread.parser.IMethodInfo;
import org.checkthread.parser.IPutGetFieldInfo.FieldAccessEnum;

/**
 * Utility class for handling set and get
 * field byte code instructions.
 */
final public class ProcessInstruction_SetGetField {
	
	private static void logInfo(String msg) {
		Log.logByteInfo(msg);
	}
	
	static void processField(int opcode, 
			OperandStack stack,
			ConstantPool constant_pool, 
			ByteReader bytes,
			JavaClass jClass,
			IMethodInfo parentMethodInfo,
			int invokedLineNumber,
			boolean isSynchronized,
			ArrayList<IClassFileParseHandler> handlerList) throws IOException {

		int index = bytes.readShort();
		boolean isStaticField = false;
		boolean isFinal = false;

		ConstantFieldref cfr = (ConstantFieldref) constant_pool.getConstant(
				index, Constants.CONSTANT_Fieldref);
		
		ConstantClass cc = (ConstantClass)constant_pool.getConstant(cfr.getClassIndex());
		ConstantNameAndType cnat= (ConstantNameAndType) constant_pool.getConstant(cfr.getNameAndTypeIndex());
		
		String fieldParentClassName = (String)cc.getConstantValue(constant_pool);
		fieldParentClassName = Utility.compactClassName(fieldParentClassName, false);				
		String fieldVariableName = cnat.getName(constant_pool);
		String signature = cnat.getSignature(constant_pool);
		String fieldTypeClassName = Utility.signatureToString(signature,false);
		String putFieldTypeClassName = null;
		
		Field[] fields = jClass.getFields();
		for(Field field : fields) {
			if(field.getName().equals(fieldVariableName)) {
				isFinal = field.isFinal();
				break;
			}
		}

		ProcessInstruction.logInfo("fieldVariableName: " + fieldVariableName);
		ProcessInstruction.logInfo("fieldTypeClass: " + fieldTypeClassName);
		ProcessInstruction.logInfo("fieldParentClass: " + fieldParentClassName);
		
		String fullFieldName = fieldParentClassName + "." + fieldVariableName;
		logInfo("processField, stack size: " + stack.size());			
		FieldAccessEnum accessEnum = FieldAccessEnum.UNDEFINED;
		
		switch (opcode) {
		
		// GETFIELD - Fetch field from object
		// Stack: ..., objectref -> ..., value
		// OR
		// Stack: ..., objectref -> ..., value.word1, value.word2
		case Constants.GETFIELD: {
			isStaticField = false;
			accessEnum = FieldAccessEnum.GET;
			logInfo("GETFIELD, stack size: " + stack.size());
			Type t = stack.pop();
			if(t instanceof ObjectType) {
				ObjectType ot3 = new FieldReferenceType(fieldVariableName,
						                                fieldTypeClassName,
						                                fieldParentClassName,
						                                fullFieldName,
						                                isStaticField);
                stack.push(ot3);
                

			} else {
				ObjectType unknown = new ObjectType("dummy");
				stack.push(unknown);
			}
			break;
		}

		// GETSTATIC - Fetch static field from class
		// Stack: ..., -> ..., value
		// OR
		// Stack: ..., -> ..., value.word1, value.word2
		case Constants.GETSTATIC:
		{
			isStaticField = true;
			accessEnum = FieldAccessEnum.GET;
			logInfo("GETSTATIC, stack size: " + stack.size());
			ObjectType ot3 = new FieldReferenceType(fieldVariableName,
                    fieldTypeClassName,
                    fieldParentClassName,
                    fullFieldName,
                    isStaticField);

			stack.push(ot3);
			break;
		}

		// PUTFIELD - Put field in object
		// Stack: ..., objectref, value -> ...
		// OR
		// Stack: ..., objectref, value.word1, value.word2 -> ...
		case Constants.PUTFIELD:  {
			accessEnum = FieldAccessEnum.PUT;
			logInfo("PUTFIELD ,stack size: " + stack.size());
			if(stack.size()==1) {
				Log.logByteInfo("ERROR: Attempting to pop empty stack");
				Log.logByteInfo(parentMethodInfo.getFullUniqueMethodName());
			}
			Type fieldValue = stack.pop();
			putFieldTypeClassName = fieldValue.getSignature();	
			try {
			   putFieldTypeClassName = Utility.signatureToString(putFieldTypeClassName);
			} catch(Exception e) {
				putFieldTypeClassName = null;
			}
			stack.pop();
			break;
		}
		
		// PUTSTATIC - Put static field in class
		// Stack: ..., value -> ...
		// OR
		// Stack: ..., value.word1, value.word2 -> ...
		case Constants.PUTSTATIC:  {
			isStaticField = true;
			accessEnum = FieldAccessEnum.PUT;
			logInfo("PUTSTATIC, stack size: " + stack.size());
			
			Type fieldValue = stack.pop();
			putFieldTypeClassName = fieldValue.getSignature();
			try {
				   putFieldTypeClassName = Utility.signatureToString(putFieldTypeClassName);
			} catch(Exception e) {
					putFieldTypeClassName = null;
			}
			break;
		}
		
		default:{
			Log.severe("Unrecognized field operation");
		}
		}
		

		// put field
		if (accessEnum == FieldAccessEnum.PUT) {
			PutFieldInfoImpl fieldInfo = new PutFieldInfoImpl(fullFieldName,
					fieldTypeClassName, 
					putFieldTypeClassName,
					parentMethodInfo, 
					invokedLineNumber, 
					isSynchronized,
					isFinal);
			
			// pass bean to handlers
			if (handlerList != null) {
				for (IClassFileParseHandler handler : handlerList) {
					handler.handlePutField(fieldInfo);
				}
			}
			
		// get field
		} else {
			GetFieldInfoImpl fieldInfo = new GetFieldInfoImpl(fullFieldName,
					fieldTypeClassName,
					parentMethodInfo, invokedLineNumber, isSynchronized);
			
			// pass bean to handlers
			if (handlerList != null) {
				for (IClassFileParseHandler handler : handlerList) {
					handler.handleGetField(fieldInfo);
				}
			}
		}
		
		PutGetFieldInfoImpl fieldInfo = new PutGetFieldInfoImpl(
				accessEnum,
                fullFieldName,
                parentMethodInfo,
                invokedLineNumber,
                isSynchronized,
                isStaticField);
        
		// pass bean to handlers
		if (handlerList != null) {
			for (IClassFileParseHandler handler : handlerList) {
				handler.handlePutGetField(fieldInfo);
			}
		}

	}
}
