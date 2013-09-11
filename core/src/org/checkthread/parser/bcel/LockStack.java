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

import java.util.*;
import java.util.logging.*;

import org.apache.bcel.generic.*;
import org.checkthread.config.Log;

/**
 * Keeps track of locking stack
 * This will be used for finding deadlocks
 */
final public class LockStack {

	private static Logger sLogger = Logger.getLogger(LockStack.class.getName());
	private Stack<Type> fStack = new Stack<Type>();
	
	public LockStack(boolean isParentMethodSynchronized) {
		if(isParentMethodSynchronized) {
			push(ObjectType.UNKNOWN);
		}
	};

	private LockStack(Stack<Type> stack) {
		fStack = stack;
	};
	
	// makes deep copy
	public LockStack getClone() {		
	   Stack<Type> stack = new Stack<Type>();
	   stack.addAll(fStack);
	   return new LockStack(stack);
	}
	
	public void push(Type t) {
		Log.logByteInfo("LOCK PUSH");
		fStack.push(t);
	}
	
	public Type pop() {
		Type retval = null;
		Log.logByteInfo("LOCK POP");
		if (!fStack.isEmpty()) {
			retval = fStack.pop();
		} else {
			//Log.reportError("Error, attempt to pop empty lock stack");
		}
		return retval;
	}
	
	public Type peek() {
		return fStack.peek();
	}
	
	public int debugGetSize() {
		return fStack.size();
	}
	public boolean isSynchronized() {
		return fStack.size()>0;
	}
}
