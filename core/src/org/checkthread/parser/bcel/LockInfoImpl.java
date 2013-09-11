package org.checkthread.parser.bcel;

import org.checkthread.parser.*;

/**
 * Lock information value object.
 */
final public class LockInfoImpl implements ILockInfo {
    
	private String fFullFieldName;
	private IMethodInfo fParentMethodInfo;
	private int fLineNumber;
	private String fShortName;
	
	public LockInfoImpl(IMethodInfo parentMethodInfo,
			            String fullFieldName,
			            String shortName,
			            int lineNumber) {
	   fFullFieldName = fullFieldName;	
	   fLineNumber = lineNumber;
	   fParentMethodInfo = parentMethodInfo;
	   fShortName = shortName;
	}

	public int getLineNumber() {
		return fLineNumber;
	}
	
	public IMethodInfo getParentMethodInfo() {
		return fParentMethodInfo;
	}
	
	public String getFullFieldName() {
		return fFullFieldName;  	
    }
	
	public String getShortName() {
		return fShortName;
	}
}
