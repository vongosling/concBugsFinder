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

import org.checkthread.parser.IInvokeMethodInfo;
import org.checkthread.parser.ILockInfo;
import org.checkthread.parser.IMethodInfo;
import org.checkthread.main.*;
import org.checkthread.policy.*;

/**
 * Class for determining synchronization loops (potential deadlocks)
 * This class is currently disabled.
 * 
 * This algorithm implored an "Adjacency List" described
 * in Algorithms, MIT Press, 1997, Cormen, top of page 466
 */
final public class LockAdjacencyListManager {

	private static ArrayList<ArrayList<INode>> sMainList = new ArrayList<ArrayList<INode>>();
	private static ArrayList<ICheckThreadError>	sErrorList = new ArrayList<ICheckThreadError>();
	private static Stack<INode> sStack = new Stack<INode>();
	private static CrossNodeTable sCrossTable = new CrossNodeTable();
	private static LoopNodeTable sLoopTable = new LoopNodeTable();
	
	public static void handleStartMethod(IMethodInfo info) {
		// handleStartMethodHelper(info);
	}
	
	public static void clearCache() {
		//clearCacheHelper();
	}
	
	public static void handleInvokeMethod(IInvokeMethodInfo info) {
	    // handleInvokeMethodHelper(info);	
	}
	
	public static void handlePushLock(IMethodInfo info, ILockInfo lockInfo) {
		// handlePushLockHelper(IMethodInfo info, ILockInfo lockInfo);
	}
	
	public static void handlePopLock(IMethodInfo info) {
		// handlePopLockHelper(info);
	}
	
	public static void handleStopMethod(IMethodInfo info) {
		// handleStopMethodHelper(info);
	}
	
	public static ArrayList<ICheckThreadError> getErrors() {
		// getErrorsHelpers();
		return new ArrayList<ICheckThreadError>();
	}
	
	private static void clearCacheHelper() {
		sMainList.clear();
		sErrorList.clear();
		sStack.clear();
		sCrossTable = new CrossNodeTable();
		sLoopTable = new LoopNodeTable();
	}

	private static void handleStartMethodHelper(IMethodInfo info) {
		sStack.clear();

		String fullName = info.getFullUniqueMethodName();
		String shortName = info.getMethodName();
		
		NodeMethod nodeMethod = NodeMethodFactory.getNodeMethod(fullName,
				shortName,
				info);	    
	    addNewLinkedList(nodeMethod);
	    
		sStack.push(nodeMethod);
		
		if(info.isSynchronized()) {
            if(info.isStaticMethod()) {
 			   fullName = info.getClassName() + ".class";
			   shortName = "class";            	
            } else {
			   fullName = info.getClassName() + ".this";
			   shortName = "this";
            }
		    NodeLock nodeLock = NodeLockFactory.getNodeLock(
		    		fullName, shortName);
            pushLockHelper(nodeLock);
		}
	}

	private static void handleInvokeMethodHelper(IInvokeMethodInfo info) {
		String methodName = info.getInvokedFullMethodName();
		String shortName = info.getInvokedMethodName();
		
		NodeMethod nodeMethod = NodeMethodFactory.getNodeMethod(methodName,
				shortName,
				null);
		appendToCurrentList(nodeMethod);
	}
	
	private static void handlePushLockHelper(IMethodInfo info, ILockInfo lockInfo) {
		NodeLock nodeLock = NodeLockFactory.getNodeLock(lockInfo
				.getFullFieldName(), lockInfo.getShortName());
        pushLockHelper(nodeLock);
	}
	
	private static void pushLockHelper(NodeLock nodeLock) {
		appendToCurrentList(nodeLock);
		sStack.push(nodeLock);
		removeLastListIfOne();
		addNewLinkedList(nodeLock);		
	}
	
	private static void handlePopLockHelper(IMethodInfo info) {	
		if (sStack.size() > 0) {
			sStack.pop();
			if (sStack.size() > 0) {
				INode node = sStack.peek();
				removeLastListIfOne();
				addNewLinkedList(node);
			}
		} else {
			// error
		}
	}
	
	private static void handleStopMethodHelper(IMethodInfo info) {
		removeLastListIfOne();
	}

	private static void addNewLinkedList(INode seedNode) {
		// create new linked list
		ArrayList<INode> nodeList = new ArrayList<INode>();
		nodeList.add(seedNode);
		sMainList.add(nodeList);
	}
	
	private static void removeLastListIfOne() {
		// remove current linked list if the length is one
		if(sMainList.size() > 0) {			
			if(sMainList.get(sMainList.size()-1).size()==1) {
			    sMainList.remove(sMainList.size() - 1);
			}
		}
	}

	private static void appendToCurrentList(INode node) {
		// append to list
		if (sMainList.size() > 0) {
			sMainList.get(sMainList.size() - 1).add(node);
		}
	}

	private static ArrayList<ICheckThreadError> getErrorsHelpers() {
		//dump2();
		// analysis occurs here
		depthFirstSearch();
		dump();
		sLoopTable.dump();
		sCrossTable.dump();
		return sLoopTable.analyze(sCrossTable);
	}

	private static class TimeCount {
		private int value = 0;
		void setTime(int t) {
			System.out.println("settime: " + t);
			value = t;
		}
		int getTime() {return value;}
	}
	
	// reference: Algorithms, page 478
	private static void depthFirstSearch() {
		TimeCount time = new TimeCount();
		time.setTime(0);
		for (ArrayList<INode> nodeList : sMainList) {
			INode headNode = nodeList.get(0);
			if (headNode.getColor() == INode.Color.WHITE) {
				NodeMethod lastMethod = null;
				NodeLock lastLock = null;
				if(headNode instanceof NodeMethod) {
					lastMethod = (NodeMethod)headNode;
				} else if(headNode instanceof NodeLock) {
					lastLock = (NodeLock)headNode;
				}
				depthFirstVisit(headNode, 
						lastMethod, 
						lastLock,
						time);
			}
		}
	}

	// reference: Algorithms, page 478
	private static void depthFirstVisit(INode headNode, 
			NodeMethod lastMethod, 
			NodeLock lastLock, 
			TimeCount time) {

		int headMCount = headNode.getMethodPredecessorCount();
		int headLCount = headNode.getLockPredessorCount();
		
		System.out.println("Head Node: " 
				 + headNode.getName() + " " + 
					headMCount + " " + headLCount);
		boolean headNodeIsMethod = false;
		boolean headNodeIsLock = false;
		
		if(headNode instanceof NodeMethod) {
            if(((NodeMethod)headNode).isEntry()) {
            //if(true) {
    			lastMethod = (NodeMethod)headNode;
    			headNodeIsMethod = true;            	
            }
		} else if(headNode instanceof NodeLock) {
			lastLock = (NodeLock)headNode;
			headNodeIsLock = true;
		}
		
		ArrayList<INode> adjacentNodeList = getAdjacentNodesForHeadNode(headNode);
		
		headNode.setColor(INode.Color.GREY);
		//System.out.println("start time " + time.getTime() + " " + headNode.getName());
		time.setTime(time.getTime()+1);
		headNode.setStartTime(time.getTime());
		
		for (INode adjNode : adjacentNodeList) {
			if (!adjNode.equals(headNode)) {
				
				headMCount = headNode.getMethodPredecessorCount();
				headLCount = headNode.getLockPredessorCount();

				int currMCount = adjNode.getMethodPredecessorCount();
				int currLCount = adjNode.getLockPredessorCount();

				
				// book keeping
				if (adjNode.getColor() == INode.Color.WHITE) {
					if (headNodeIsMethod) {
						headMCount++;
					} else {
						headLCount++;
					}
				}
				
				System.out.println("Adj Node: " + adjNode.getName() + " " + 
						headMCount + " " + headLCount + " " 
						+ currMCount + " " + currLCount);
				
				// down
				if (adjNode.getColor() == INode.Color.WHITE) {
                    adjNode.setLockPredessorCount(headLCount);
                    adjNode.setMethodPredecessorCount(headMCount);
                    
					adjNode.setLastMethodPredecessor(lastMethod);
					adjNode.setLastLockPredecessor(lastLock);
					adjNode.setPredecessor(headNode);
					
					if(adjNode instanceof NodeMethod) {
					    if(((NodeMethod)adjNode).isEntry()) {
					    	
					    	// to do: remove this
					    	// inefficient, come up with a better
					    	// way to track nodes within a loop
					    	sCrossTable.addCross(null, adjNode);
					    }
					}
					
					depthFirstVisit(adjNode, 
							lastMethod, 
							lastLock,
							time);
					
				// back
				} else if (adjNode.getColor() == INode.Color.GREY) {
					sLoopTable.addLoop(headNode, adjNode);

				// cross
				} else {
					sCrossTable.addCross(headNode, adjNode);
					/*
					if(headMCount>currMCount) {
						adjNode.setMethodPredecessorCount(headMCount);
					}
					if(headLCount>currLCount) {
						adjNode.setLockPredessorCount(headLCount);
					}	
					*/				
				}
			}
		}
		headNode.setColor(INode.Color.BLACK);
		
		//System.out.println("end time " + time.getTime() + " " + headNode.getName());
        time.setTime(time.getTime()+1);
		headNode.setEndTime(time.getTime());
	}

	// helper function
	private static ArrayList<INode> getAdjacentNodesForHeadNode(INode headNode) {
		ArrayList<INode> adjNodeList = new ArrayList<INode>();
		for (ArrayList<INode> nodeList : sMainList) {
			INode headNodeInner = nodeList.get(0);
			if (headNodeInner.equals(headNode)) {
				for (INode node : nodeList) {
					if (node.equals(headNode)) {
						adjNodeList.addAll(nodeList);
						adjNodeList.remove(headNode);
					}
				}
			}
		}
		return adjNodeList;
	}

	private static void dump2() {
		
		System.out.println("DUMP");
		for(ArrayList<INode> list : sMainList) {
			StringBuffer str = new StringBuffer();
		    for(INode node : list) {
		    	node.setEndTime(0);
		    	node.setStartTime(0);
		      	str.append(" -> " + node.getName() 
		      			+ " " + node.getStartTime() + "/" + node.getEndTime() 
		      			+ " " + node.getLockPredessorCount() + "|" + node.getMethodPredecessorCount());
		    }
		    System.out.println(str); 
		}
	}
	
	private static void dump() {
		
		System.out.println("DUMP");
		for(ArrayList<INode> list : sMainList) {
			StringBuffer str = new StringBuffer();
		    for(INode node : list) {
		      	str.append(" -> " + node.getName() 
		      			+ " " + node.getStartTime() + "/" + node.getEndTime()
		      	+ " " + node.getMethodPredecessorCount() + "|" + node.getLockPredessorCount());
		    }
		    System.out.println(str); 
		}
	}
	
	
}
