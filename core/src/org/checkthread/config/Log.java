/*
Copyright (c) 2009 Joe Conti CheckThread.org

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

package org.checkthread.config;

import org.checkthread.main.*;

import java.util.logging.Logger;

/**
 * Wrapper object for logging. 
 * ToDo: Refactor and use a more formal logging mechanism
 * like java.util.Logger.
 *
 */
public class Log {
	private static Logger sLogger = Logger.getLogger(Log.class.getName());
	private static Verbose sVerboseLevel = Verbose.NONE;
	public static void setVerboseLevel(Verbose v) {sVerboseLevel=v;}
	
	private static InputBean sParamBean;
	
	public static void toolInfo(String msg) {
	   if (sVerboseLevel == Verbose.LOW 
				|| sVerboseLevel == Verbose.MED
				|| sVerboseLevel == Verbose.HIGH) {
			sLogger.info(msg);
		}
	}
	
	public static void reportError(String msg) {
	    System.err.println(msg);	
	}
	
	public static void reportThreadPolicyError(String msg) {
	    System.err.println(msg);	
	}
	
	public static void debugInfo(String msg) 
	{
		if(sVerboseLevel==Verbose.LOW || 
		   sVerboseLevel==Verbose.MED ||
		   sVerboseLevel==Verbose.HIGH) 
		{
		    sLogger.info(msg); 
		}
	}
	
	public static void severe(String msg) {
		System.err.println("severe: " + msg);
	}
	
	public static void reportException(Exception e) {
		sLogger.severe("exception: " + e.toString());
	}
	
	public static void logByteInfo(String msg) {
		//System.out.println(msg);
	}
}
