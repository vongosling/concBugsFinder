package org.checkthread.parser.bcel;

import org.checkthread.main.Constants;
import org.checkthread.parser.IMethodInfo;
import org.checkthread.parser.IPutFieldInfo;

public class PutFieldInfoImpl implements IPutFieldInfo {
	
    private IMethodInfo fParentMethodInfo;
    private String fFullFieldName;   
    private String fDeclaredClassName;
    private String fPutClassName;
    private int fInvokeLineNumber = Constants.NO_LINE_NUMBER;
    private boolean fIsSynchronized;
    private boolean fIsFinal;
        
    public PutFieldInfoImpl(
            String fullFieldName,
            String declaredFieldClassName,
            String putFieldClassName,
            IMethodInfo parent,
            int invokeLineNumber,
            boolean isSynchronized,
            boolean isFinal
            )
    {
    	fIsFinal = isFinal;
    	fFullFieldName = fullFieldName;
    	fDeclaredClassName = declaredFieldClassName;
    	fPutClassName = putFieldClassName;
        fParentMethodInfo = parent;
        fInvokeLineNumber = invokeLineNumber;
        fIsSynchronized = isSynchronized;
    }

    public boolean isFinal() {return fIsFinal;}
    
    public String getDump() {
    	return ", fullFieldName: " + fFullFieldName + 
    	       ", parent: " + fParentMethodInfo.getMethodName();
    }
    
    public String getDeclaredFieldClassName() {
    	return fDeclaredClassName;
    }
    
    public String getPutFieldClassName() {
    	return fPutClassName;
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
