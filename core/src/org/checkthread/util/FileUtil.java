/*
Copyright (c) 2008 Joe Conti, CheckThread.org

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

package org.checkthread.util;

import java.io.*;
import java.util.*;
import java.net.*;

import org.checkthread.config.Log;

public class FileUtil {
     
    public static File[] getSubDirectoriesForFile(File file) {
		DirFilter filter = new DirFilter();
		File[] dirList = file.listFiles(filter);
		return dirList;
    }
	
    public static ArrayList<URL> createURLListFromURIList(ArrayList<URI> uriList) {
		ArrayList<URL> retval = new ArrayList<URL>();
		for (URI uri : uriList) {
			try {
				retval.add(uri.toURL());
			} catch (Exception e) {
				Log.reportError(e.getMessage());
			}
		}
		return retval;
	}
	
	public static URL[] createURLFromURIArray(ArrayList<URI> uriList) {
		int len = uriList.size();
		URL[] urls = new URL[len];
		int n = 0;
		for (URI uri : uriList) {
			try {
				urls[n++] = uri.toURL();
				Log.debugInfo("URL: " + urls[n-1]);
			} catch (Exception e) {
				Log.reportError(e.getMessage());
			}
		}
		return urls;
	}
	
	public static ArrayList<File> getEnclosingClassesForPrimaryClass(File classFile) {
        Log.toolInfo("getEnclosingClassesForPrimaryClass: " + classFile);
		ArrayList<File> list = new ArrayList<File>();
    	String parentDir = getParentDir(classFile.getAbsolutePath());
    	Log.toolInfo("parentDir: " + parentDir);
    	String className = FileUtil.getShortClassNameFromFullPathString(classFile.toString());
    	Log.toolInfo("className: " + className);
    	File dir = new File(parentDir);
    	File[] innerClassList = dir.listFiles(new EnclosingClassFilter(className));
    	for(File f : innerClassList) {
    		Log.toolInfo("Found: " + f);
    		list.add(f);
    	}
    	return list;	
	}
    
	// get all the class files within the input directory
	public static ArrayList<File> loadClassFilesForDir(File rootFile) {
	    ArrayList<File> retval = new ArrayList<File>();
	    File[] classFiles = null;
	    ClassFileFilter filter = new ClassFileFilter();
	    if(rootFile.isDirectory()) {
	        classFiles = rootFile.listFiles(filter);
	        classFiles = sortClassFiles(classFiles);
	        for(File f : classFiles) {
	    	    retval.add(f);
	        }
	    }
	    return retval;
	}
	
	// move inner classes to the end of the list
	private static File[] sortClassFiles(File[] classFiles) {
		File[] sortedClassFiles = new File[classFiles.length];
		
		int outerClassInd = 0;
		int innerClassInd = classFiles.length-1;
		for(File file : classFiles) {
		    if(file.getAbsolutePath().contains("$")) {
		    	sortedClassFiles[innerClassInd--] = file;
		    } else {
		    	sortedClassFiles[outerClassInd++] = file;
		    }
		}
		return sortedClassFiles;
	}
	
	//ToDo: Clean this up, use regexp
    private static String getShortClassNameFromFullPathString(String classFile) {
    	String retval = null;
    	int ind1 = classFile.lastIndexOf("/");
    	int ind2 = classFile.lastIndexOf("\\"); 
    	int endind = (ind1>ind2) ? ind1 : ind2;
    	String CLASS = ".class";
    	retval = classFile.substring(endind+1, classFile.length()-CLASS.length());
    	return retval;
    }
    
    // ToDo: use regexp
    private static String getParentDir(String classFile) {
    	String retval = null;
    	int ind1 = classFile.lastIndexOf("/");
    	int ind2 = classFile.lastIndexOf("\\"); 
    	int endind = (ind1>ind2) ? ind1 : ind2;
    	retval = classFile.substring(0, endind);
    	return retval;
    }
    

	// directory filter
	private static class DirFilter implements FileFilter
	{
		public boolean accept(File file) {
			return file!=null && file.isDirectory();
		}
	}
	
	// class file filter
	private static class ClassFileFilter implements FileFilter
	{
		public boolean accept(File file) {
			return file!=null 
			       && file.isFile() 
			       && file.getAbsolutePath().endsWith(".class");
		}
	}
	
    private static class EnclosingClassFilter implements FilenameFilter {
		private String fRootClassName;

		public EnclosingClassFilter(String rootClassName) {
			fRootClassName = rootClassName;
		}

		public boolean accept(File dir, String name) {
			return name.startsWith(fRootClassName + "$");
		}
	}
}
