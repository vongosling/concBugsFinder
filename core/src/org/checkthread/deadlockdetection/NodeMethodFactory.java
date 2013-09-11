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

package org.checkthread.deadlockdetection;

import java.util.HashMap;

import org.checkthread.parser.*;

final public class NodeMethodFactory {

	private static java.util.HashMap<String, NodeMethod> fCache = new HashMap<String, NodeMethod>();
	
	public static void clearCache() {
		fCache.clear();
	}
	
	public static NodeMethod getNodeMethod(String fullMethodName, 
			String shortName,
			IMethodInfo methodInfo) {
		
		// get from cache
		NodeMethod node = fCache.get(fullMethodName);
		if(node==null) {
		   node = new NodeMethod(fullMethodName, 
				   shortName, 
				   methodInfo);
		   fCache.put(fullMethodName, node);
		}
		
		// add method info if we haven't already
		if(node.getMethodInfo()==null) {
			node.setMethodInfo(methodInfo);
		}
		
		return node;
	}

}