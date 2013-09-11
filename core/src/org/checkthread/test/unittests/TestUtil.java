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

package org.checkthread.test.unittests;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import org.checkthread.config.*;
import org.checkthread.main.ICheckThreadError;
import org.checkthread.parser.*;
import org.checkthread.util.*;

final public class TestUtil {
    
	private static String sRootDir = "";
	final private static String DIR = "/";
    
    static void setCheckDir(String rootDir) {
    	sRootDir = rootDir + DIR;
    }
    
    static String getCheckDir() {
    	return sRootDir;
    }
    
    public static File getFullPathFromClass(Class cls) {
        String className = cls.getName();
        String path = className.replace(".",DIR)+".class";
        String retval = sRootDir + path;
        File file = new File(retval);
        if(!file.isFile()) {
        	Log.severe("Invalid file: " + file.toString());
        }
        return new File(retval);
    }
    
    public static TestParseHandler parseClassHelper(String classFile) {
        TestParseHandler handler = new TestParseHandler();
        HashMap<String, AccessibleObject> mapSynthetic = new HashMap<String,AccessibleObject>();        
        IClassFileParser parser = ClassFileParserFactory.getClassFileParser(mapSynthetic);
        parser.addHandler(handler);
        String fullPath = sRootDir + classFile;
        parser.parseClassFile(new File(fullPath));
        return handler;       
    }
    
    public static TestParseHandler parseClassHelper(Class cls) {
    	File fullPath = TestUtil.getFullPathFromClass(cls);
    	HashMap<String, AccessibleObject> mapSynthetic = new HashMap<String,AccessibleObject>();
    	ArrayList<File> enclosingClasses = FileUtil.getEnclosingClassesForPrimaryClass(fullPath);
    	
    	TestParseHandler handler = new TestParseHandler();
                IClassFileParser parser = ClassFileParserFactory.getClassFileParser(mapSynthetic);
        parser.addHandler(handler);    
        parser.parseClassFile(fullPath);
        
        for(File enclosedClass : enclosingClasses) {
        	parser.parseClassFile(enclosedClass);
        }
        
        return handler;
    }
    
    public static boolean verifyNoThreadPolicyErrors(Class testCase) {
        TestParseHandler handler = TestUtil.parseClassHelper(testCase);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
        
        // Verify that there are no thread policy errors
        return list.size()==0;
    }
}
