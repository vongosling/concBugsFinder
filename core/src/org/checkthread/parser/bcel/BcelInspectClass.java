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

package org.checkthread.parser.bcel;

import java.io.*;
import java.util.*;

import org.apache.bcel.classfile.*;
import org.checkthread.parser.*;
import org.checkthread.config.*;

/**
 * Utility for inspecting a java class file
 */
final public class BcelInspectClass {
    
	private ConstantPool constant_pool;
    private JavaClass fJClass;
    private ArrayList<IClassFileParseHandler> fHandlerList;
    private HashMap<String,java.lang.reflect.AccessibleObject> fHashMapSynthetic;
       
    BcelInspectClass(String zipFile, 
    		         String zipFileEntry,
			         ArrayList<IClassFileParseHandler> handlerList,
			         HashMap<String,java.lang.reflect.AccessibleObject> map) 
			         throws Exception {
    	fHashMapSynthetic = map;
    	if(fHashMapSynthetic==null) {
    		Log.severe("Error in BcelInspectClass, null hashtable");
    	}
		fHandlerList = handlerList;
		ClassParser parser = new ClassParser(zipFile,zipFileEntry);
        init(parser);
	}
    
    BcelInspectClass(File classFile,
                     ArrayList<IClassFileParseHandler> handlerList,
                     HashMap<String,java.lang.reflect.AccessibleObject> map) 
                     throws Exception 
        {
    	fHashMapSynthetic = map;
        fHandlerList = handlerList;       	
        ClassParser parser = new ClassParser(classFile.getAbsolutePath());
        try {
            init(parser);
           
        // Catch all errors so CheckThread can continue to the next class
        } catch(Throwable e) {
        	//Log.severe(e.toString());
        	e.printStackTrace();
        }
    }
    
    private void init(ClassParser parser) throws Exception {
        fJClass = parser.parse();
        constant_pool = fJClass.getConstantPool();
        inspectMethods(fJClass);
    }
    
    private void inspectMethods(JavaClass jClass) throws Exception {
        Method[] mlist = jClass.getMethods();
        Log.logByteInfo("BcelInspectClass:inspectClass: " + jClass.getClassName() + " method count " + mlist.length); 
        
        Class cls = null;
        String className = jClass.getClassName();
        
        // Loading a class may throw a NoClassDefFoundError
        // which is an error, not an exception
        try {
           cls = ClassLoaderBridge.loadClass(className);
        } catch(NoClassDefFoundError e) {
        	e.printStackTrace();
        	Log.reportError(e.toString());
        	return;
        }

        // notify handlers
        if(fHandlerList!=null) {
            for(IClassFileParseHandler handler : fHandlerList) {
                handler.handleStartClass(cls);
            }
        }
        
        // Loop through each method
        for(Method m : mlist) {
        	Log.debugInfo(" Method: " + m.getName());
        	
        	
            // catch all errors so CheckThread can continue to next 
            // method
            try {	
            	ProcessMethod.inspectMethod(
            			constant_pool, fJClass,
				fHandlerList, fHashMapSynthetic,
            			 className,m);
            } catch(Throwable e) {
            	Log.severe(e.toString());
            	e.printStackTrace();
            }
            
        }
        
        // notify handlers
        if(fHandlerList!=null) {
            for(IClassFileParseHandler handler : fHandlerList) {
                handler.handleStopClass(cls);
            }
        } 
    }
    

  
}

