package org.checkthread.parser.bcel;

import java.util.ArrayList;

import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.structurals.OperandStack;
import org.checkthread.parser.IClassFileParseHandler;
import org.checkthread.parser.IMethodInfo;
import org.checkthread.parser.ILockInfo;

import org.checkthread.config.*;

/**
 * Utility class for handling lock byte code instructions
 */
final public class ProcessInstruction_Lock {

	static void processMonitorEnter(OperandStack stack,
			ArrayList<IClassFileParseHandler> handlerList,
			IMethodInfo parentMethodInfo, 
			LockStack lockStack,
			int lineNumber) {

		Type t;
		t = stack.pop();
		String fullFieldName = null;
		String shortName = null;
		if (t instanceof FieldReferenceType) {
			FieldReferenceType ot = (FieldReferenceType) t;
			fullFieldName = ot.getFullFieldName();
			shortName = ot.getFieldName();
			ProcessInstruction.logLocking(" Obtaining lock on: "
					+ ot.getContainerClass() + "." + ot.getFieldName());	
		} else if (t instanceof ThisReferenceType) {
			ThisReferenceType ot = (ThisReferenceType) t;
			String className = ot.getClassName();
			fullFieldName = className + ".this";
			shortName = "this";
		} else {
			Log.debugInfo("lock not handled: " + t.getClass());
		}
		lockStack.push(t);

		// if lock has a field name
		if (fullFieldName != null) {
			ILockInfo lockInfo = new LockInfoImpl(parentMethodInfo,
					                              fullFieldName,
					                              shortName,
					                              lineNumber
					                              );

			// notify handlers
			if (handlerList != null) {
				for (IClassFileParseHandler handler : handlerList) {
					handler.handlePushLock(parentMethodInfo, lockInfo);
				}
			}
		}
	}
	
	static void processMonitorExit(OperandStack stack,
			ArrayList<IClassFileParseHandler> handlerList,
			IMethodInfo parentMethodInfo, 
			LockStack lockStack) {
		stack.pop();
		lockStack.pop();
		
		// notify handlers
		if (handlerList != null) {
			for (IClassFileParseHandler handler : handlerList) {
				handler.handlePopLock(parentMethodInfo);
			}
		}
		
	}
}
