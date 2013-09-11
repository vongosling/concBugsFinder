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

public abstract class AbstractNode implements INode {
	private Color fColor = Color.WHITE;
	private int fStartTime = 0;
	private int fEndTime = 0;
	private INode fNode = null;
	private NodeMethod fLastMethod = null;
    private NodeLock fLastLock = null;
	private int fLockPredCount = 0;
	private int fMethodPredCount = 0;
 
	private NodeMethod fCrossMethodPred = null;
	private boolean fIsBackNode = false;
	private String fShortName = null;
	
    abstract public String getName();
    
    public void setShortName(String s) {
    	fShortName = s;
    }
    
    public String getShortName() {
    	return fShortName;
    }
    
	public Color getColor() {
    	return fColor;
    }
    
    public void setColor(Color color) {
    	fColor = color;
    }
    
    public int getEndTime() {return fEndTime;}
    public void setEndTime(int time) {fEndTime = time;}

    public int getStartTime() {return fStartTime;}
    public void setStartTime(int time) {fStartTime = time;}
    
    public void setPredecessor(INode node) {fNode = node;}
    public INode getPredecessor() {return fNode;}
    
    public void setLastMethodPredecessor(NodeMethod node) {fLastMethod = node;}
    public INode getLastMethodPredecessor() {return fLastMethod;}
    
    public void setMethodPredecessorCount(int count) {fMethodPredCount = count;}
    public int getMethodPredecessorCount() {return fMethodPredCount;}
    public void incrementMethodPredessorCount() {
    	fMethodPredCount++;
    }
    
    public void setLockPredessorCount(int count) {fLockPredCount = count;}
    public int getLockPredessorCount() {return fLockPredCount;}
    public void incrementLockPredessorCount() {
    	fLockPredCount++;
    }    
    
    public void setCrossMethodPredecessor(NodeMethod node) {
    	fCrossMethodPred = node;
    }
    public INode getCrossMethodPredessor() {
    	return fCrossMethodPred;
    }
    
    public boolean isInLoop() {
        boolean retval = false;
        return retval;
    }
    
    public boolean isBackNode() {
    	return fIsBackNode;
    }
    
    public void setBackNode(boolean b) {
    	fIsBackNode = b;
    }
    
    public void setLastLockPredecessor(NodeLock node) {
        fLastLock = node;	
    }
    
    public INode getLastLockPredecessor() {
        return fLastLock;	
    }

}
