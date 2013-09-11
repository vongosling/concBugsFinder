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

public interface INode {
	public enum Color {WHITE, GREY, BLACK}
	
    public String getName();
    public String getShortName();
    public Color getColor();
    public void setColor(Color color);
    
    public int getEndTime();
    public void setEndTime(int time);

    public int getStartTime();
    public void setStartTime(int time);
    
    public void setPredecessor(INode node);
    public INode getPredecessor();
    
    public void setLastMethodPredecessor(NodeMethod node);
    public INode getLastMethodPredecessor();

    public void setLastLockPredecessor(NodeLock node);
    public INode getLastLockPredecessor();
    
    //public void setCrossMethodPredecessor(NodeMethod node);
    //public INode getCrossMethodPredessor();

    public void setMethodPredecessorCount(int count);
    public int getMethodPredecessorCount();
    public void incrementMethodPredessorCount();
    
    public void setLockPredessorCount(int count);
    public int getLockPredessorCount();
    public void incrementLockPredessorCount();
    
    public boolean isInLoop();
    public boolean isBackNode();
    public void setBackNode(boolean b);
}
