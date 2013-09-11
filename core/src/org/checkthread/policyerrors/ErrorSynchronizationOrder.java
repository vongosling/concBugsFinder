package org.checkthread.policyerrors;

import org.checkthread.main.*;
import org.checkthread.parser.IMethodInfo;

public class ErrorSynchronizationOrder implements ICheckThreadError {
    
	private final IMethodInfo fMethodInfo;
	private final String fLoopMethod;
	private final String fLock1;
	private final String fLock2;
	
	public ErrorSynchronizationOrder(IMethodInfo methodInfo,
			                         String loopMethod,
			                         String lock1, 
			                         String lock2) 
	{
        fMethodInfo = methodInfo;
        fLoopMethod = loopMethod;
        fLock1 = lock1;
        fLock2 = lock2;
    }

	public String getErrorMessage() {
			return "Method " + fMethodInfo.getMethodName() + 
			" has a different order for locks " + fLock1 + " and " + fLock2 + 
			" than method " + fLoopMethod;
	}
	
	public void printErr() {
		System.out.println(getErrorMessage());
	}
	
	public String getParentName() {
		return  fMethodInfo.getMethodName();
	}
	public String getInvokedName() {
		return "not applicable";
	}
	
	public int getLineNumber() {
		return  fMethodInfo.getLineNumber();
	}
	
	public String getSourceFile() {
		return fMethodInfo.getSourceFile();
	}
	
	public String getClassFilePath() {
		return fMethodInfo.getPathToClassFile();
	}
}
