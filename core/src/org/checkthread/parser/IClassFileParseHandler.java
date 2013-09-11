/*
Copyright (c) 2008 Joe Conti CheckThread.org

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

/**
 * Implementations of this interface can plugin
 * to the CheckThread parser and handle static analysis
 * events.
 *
 */
public interface IClassFileParseHandler {
    
	public void handleStartClass(Class cls);

	/**
	 * Return true if CheckThread should step through
	 * this method's byte code.
	 * Return false to skip 
	 * Note: The output argument is not used. The intent
	 * was to provide a performance optimization but things
	 * did not speed up at all.
	 * @param info
	 * @return
	 */
    public boolean handleStartMethod(IMethodInfo info);
    
    public void handlePushLock(IMethodInfo info, ILockInfo lockInfo);
    
    public void handlePopLock(IMethodInfo info);
    
    public void handleStopMethod(IMethodInfo info);
    
    public void handleInvokeMethod(IInvokeMethodInfo info);
    
    public void handlePutGetField(IPutGetFieldInfo info);
   
    public void handlePutField(IPutFieldInfo info);
    
    public void handleGetField(IGetFieldInfo info);
    
    public void handleStopClass(Class cls);
}
