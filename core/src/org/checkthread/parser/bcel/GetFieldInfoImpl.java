package org.checkthread.parser.bcel;

import org.checkthread.main.Constants;
import org.checkthread.parser.IMethodInfo;
import org.checkthread.parser.IGetFieldInfo;

public class GetFieldInfoImpl implements IGetFieldInfo {
	
    private IMethodInfo fParentMethodInfo;
    private String fFullFieldName;    
    private String fDeclaredClassName;
    private int fInvokeLineNumber = Constants.NO_LINE_NUMBER;
    private boolean fIsSynchronized;
        
    public GetFieldInfoImpl(
            String fullFieldName,
            String declaredFieldClassName,
            IMethodInfo parent,
            int invokeLineNumber,
            boolean isSynchronized
            )
    {
    	fFullFieldName = fullFieldName;
    	fDeclaredClassName = declaredFieldClassName;
        fParentMethodInfo = parent;
        fInvokeLineNumber = invokeLineNumber;
        fIsSynchronized = isSynchronized;
    }

    public String getDump() {
    	return ", fullFieldName: " + fFullFieldName + 
    	       ", parent: " + fParentMethodInfo.getMethodName();
    }
      
    public String getDeclaredFieldClassName() {
    	return fDeclaredClassName;
    }
    
    public String getFullFieldName() {
    	return fFullFieldName;
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
}
