package org.checkthread.parser;

public interface IFieldInfo {
	public boolean isSynchronized();
    public boolean isStaticBlock();       
    public IMethodInfo getParentMethodInfo();        
    public String getFullFieldName();
	public String getDeclaredFieldClassName();
	
    // for reporting
    public int getLineNumber();
    public String getPathToClassFile();    
    public String getSourceFile();
    
    // for debugging
    public String getDump();
}
