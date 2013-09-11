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

package org.checkthread.main;

/**
 * Interface that represents a CheckThread error.
 * This interface is exposed to clients (e.g. IDE Plugins)
 *
 */
public interface ICheckThreadError {
	
	/**
	 * For debugging, print error to std out
	 */
	public void printErr();
	
	/**
	 * Get parent method name
	 * @return
	 */
	public String getParentName();
	
	/**
	 * Get invoked method name
	 * @return
	 */
	public String getInvokedName();
	
	/**
	 * Get line number for error
	 * @return
	 */
	public int getLineNumber();
	
	/**
	 * Get path to source file
	 * @return
	 */
	public String getSourceFile();
	
	/**
	 * Get path to class file
	 * @return
	 */
	public String getClassFilePath();
	
	/**
	 * Get error message to display 
	 * @return
	 */
	public String getErrorMessage();
}
