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

package org.checkthread.parser;

import org.checkthread.policy.*;
import java.lang.reflect.*;

/**
 * Interface for a value object holding
 * information about a method invocation.
 *
 */
public interface IInvokeMethodInfo {
	
    public boolean isSynchronized();   
    public boolean isStaticBlock();       
    public IMethodInfo getParentMethodInfo();    
    
    // invoked info
    public String getInvokedMethodName();
    public String getInvokedFullMethodName();
    public IThreadPolicy getInvokedThreadPolicy();   
    public String getInvokedFieldName();   
    public boolean isInvokedMethodOnNonStaticField();
    public boolean isInvokedMethodOnStaticField();
    public boolean isInvokedMethodStatic();
    public boolean isInvokedMethodOnThis();
    public AccessibleObject getMethod();
    public void setInvokedThreadPolicy(IThreadPolicy policy);
    
    // for reporting
    public int getLineNumber();
    public String getPathToClassFile();    
    public String getSourceFile();
}
