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

import org.checkthread.main.Constants;
import org.checkthread.parser.*;

/**
 * Value object holding information about a put 
 * or get field.
 */
public class PutGetFieldInfoImpl implements IPutGetFieldInfo {
		
	private FieldAccessEnum fFieldAccessEnum;
    private IMethodInfo fParentMethodInfo;
    private String fFullFieldName;
    
    private int fInvokeLineNumber = Constants.NO_LINE_NUMBER;
    private boolean fIsSynchronized;
    private boolean fIsStaticField;
        
    public PutGetFieldInfoImpl(
            FieldAccessEnum accessEnum,
            String fullFieldName,
            IMethodInfo parent,
            int invokeLineNumber,
            boolean isSynchronized,
            boolean isStaticField
            )
    {
    	fFieldAccessEnum = accessEnum;
    	fFullFieldName = fullFieldName;
        fParentMethodInfo = parent;
        fInvokeLineNumber = invokeLineNumber;
        fIsSynchronized = isSynchronized;
        fIsStaticField = isStaticField;
    }
    
    public String getDump() {
    	return " AccessEnum: " + fFieldAccessEnum +
    	       ", fullFieldName: " + fFullFieldName + 
    	       ", parent: " + fParentMethodInfo.getMethodName();
    }
    
    public FieldAccessEnum getFieldAccess() {
    	return fFieldAccessEnum;
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
    
    public boolean isStaticField() {
    	return fIsStaticField;
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
