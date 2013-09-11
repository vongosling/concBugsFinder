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

import java.util.*;
import java.net.*;

/**
 * Specifies inputs to CheckThread engine.
 * Client should create one of these and pass to 
 * CheckThreadMainFactory.
 */
public class InputBean {
	
   private ArrayList<URI> fTargetPath = new ArrayList<URI>();
   private ArrayList<URI> fClassPath = new ArrayList<URI>();
   
   private int fVerboseLevel = 0;
   private boolean fIsRecurse = true;
   private ArrayList<ILogHandler> fLogHandlerList = new ArrayList<ILogHandler>();
   private ICheckThreadListener fCheckThreadListener = null;
   
   private InputBean() {}
   public static InputBean newInstance() {
	   return new InputBean();
   }
   
   public void setListener(ICheckThreadListener l) {
   	fCheckThreadListener = l;
   }
   
   public ICheckThreadListener getListener() {
   	return fCheckThreadListener;
   }
   
   public void addLogHandler(ILogHandler h) {
	   fLogHandlerList.add(h);
   }
   
   public void removeLogHandler(ILogHandler h) {
	   fLogHandlerList.remove(h);
   }
   
   public void logMessage(String msg) {
      for(ILogHandler h : fLogHandlerList) {
    	  h.handleMessage(msg);
      }
   }
   
   public void setTargetPath(ArrayList<URI>  targetPath) {
	   fTargetPath = targetPath;
   }
   
   public ArrayList<URI>  getTargetPath() {
	   return fTargetPath;
   }
   
   public void setRecurse(boolean b) {fIsRecurse = b;}
   
   public boolean isRecurse() {return fIsRecurse;}

   public void setClassPath(ArrayList<URI>  targetPath) {
	   fClassPath = targetPath;
   }
   
   public ArrayList<URI>  getClassPath() {
	   return fClassPath;
   }

   public int getVerboseLevel() {
	   return fVerboseLevel;
   }
   
   public void setVerboseLevel(int v) {
	   fVerboseLevel = v;
   }
}
