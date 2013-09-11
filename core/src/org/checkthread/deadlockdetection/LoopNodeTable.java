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

import java.util.*;

import org.checkthread.main.*;
import org.checkthread.policyerrors.*;
import org.checkthread.policy.*;

final public class LoopNodeTable {

	private ArrayList<Loop> table = new ArrayList<Loop>();
	
	public void addLoop(INode start, INode stop) {
		Loop loop = new Loop();
		loop.fStart = start;
		loop.fStop = stop;
		table.add(loop);
	}

	public void dump() {
		System.out.println("");
		System.out.println("***Loop Node Table***");
		for (Loop loop : table) {
			System.out.println("Start: " + loop.fStart.getShortName()
					+ " Stop: " + loop.fStop.getShortName());
		}
		System.out.println("");
	}

	public ArrayList<ICheckThreadError> analyze(CrossNodeTable crossTable) {
		ArrayList<ICheckThreadError> retval = new ArrayList<ICheckThreadError>();

		System.out.println("***ANALYZE***");
		// do analysis here
		for (Loop loop : table) {

			Iterator<Cross> i = crossTable.getTable().iterator();
			boolean foundError = false;
			while (i.hasNext() && !foundError) {
				Cross cross = i.next();
				if (NodeUtil.isIntersectingLoop(loop, cross)) {

					NodeMethod nodeMethod = (NodeMethod) loop.fStart
							.getLastMethodPredecessor();
					NodeMethod loopNodeMethod = (NodeMethod) loop.fStop
							.getLastMethodPredecessor();

					IThreadPolicy policy = nodeMethod.getMethodInfo()
							.getThreadPolicy();
					IThreadPolicy loopPolicy = loopNodeMethod.getMethodInfo()
							.getThreadPolicy();
					if (policy instanceof ThreadConfinedPolicy
							&& policy.isEquivalent(loopPolicy)) {
						 String id =
						 ((ThreadConfinedPolicy)policy).getThreadConfinedName();
						System.out.println("TESTETST: " + id);

						 id =
							 ((ThreadConfinedPolicy)loopPolicy).getThreadConfinedName();
							System.out.println("TESTETST: " + id);
							
						// no-op
					} else {
						foundError = true;
						ErrorSynchronizationOrder error = new ErrorSynchronizationOrder(
								nodeMethod.getMethodInfo(), loopNodeMethod
										.getFullMethodName(), loop.fStart
										.getLastLockPredecessor()
										.getShortName(), loop.fStop
										.getLastLockPredecessor()
										.getShortName());
						retval.add(error);

						System.out.println("DEADLOCK in "
								+ loop.fStart.getLastMethodPredecessor()
										.getShortName()
								+ " "
								+ loop.fStart.getLastLockPredecessor()
										.getShortName()
								+ " | "
								+ loop.fStop.getLastLockPredecessor()
										.getShortName());
					}

				}
			}
		}

		return retval;
	}
}
