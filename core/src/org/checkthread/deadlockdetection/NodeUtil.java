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

final public class NodeUtil {

	// Node are cross if there is no intersection
	public boolean isCross(INode startNode, INode stopNode) {
		int n1 = startNode.getStartTime();
		int n2 = startNode.getEndTime();
		int m1 = stopNode.getStartTime();
		int m2 = stopNode.getEndTime();
		
		return (n1>m1 && n2>m2) || (n1<m1 && n2<m2);
	}
	
	public static boolean isIntersectingLoop(Loop loop, Cross cross) {
		boolean retval = false;
		int n1 = cross.fStop.getStartTime();
		int n2 = cross.fStop.getEndTime();
		if(loop.fStart.equals(loop.fStop)) {
			retval = false;
		} else if (loop.fStop.getStartTime() < n1 
				&& loop.fStop.getEndTime() > n2
				&& loop.fStart.getStartTime() > n1
				&& loop.fStart.getEndTime() < n2) {
			retval = true;
		} else if(loop.fStart.equals(cross.fStop)) {
			retval = true;
		}
		return retval;
	}
}
