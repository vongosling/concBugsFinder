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

package org.checkthread.main;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.net.*;
import java.util.*;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.classfile.*;

import org.checkthread.annotations.*;
import org.checkthread.parser.*;
import org.checkthread.config.*;
import org.checkthread.util.*;
import org.checkthread.deadlockdetection.*;

/**
 * This class is the main entry point for the CheckThread static analysis 
 * engine. The API is not thread safe and should be invoked from the same thread.
 */
@NotThreadSafe
public class CheckThreadMain implements ICheckThreadMain {

	// Keep a cache of class files checked so we don't duplicate
    // Users may duplicate target directories and we don't want to analyze
	// the same file twice.
	private HashMap<String,Boolean> fCacheClassFiles = new HashMap<String,Boolean>();
	
	private ArrayList<ICheckThreadError> fErrorList = new ArrayList<ICheckThreadError>();
		
	/**
	 * Main entry point into the CheckThread engine.
	 * IDE plug-ins and other apps embedding CheckThread
	 * should call this method to kick off CheckThread analysis engine.
	 * @param inputBean
	 */
	CheckThreadMain(InputBean inputBean) {
		
		// remove previous state, if any
		GlobalCacheManager.clearCache();
		
		// ConfigBean holds global information that will be referenced 
		// throughout the CheckThread engine
		ConfigBean configBean = ConfigSingletonFactory.newConfigBean();;
		
		// Add InputBean info to ConfigBean so we can retrieve later
		configBean.setTargetPath(inputBean.getTargetPath());

		ArrayList<URL> classPath = FileUtil.createURLListFromURIList(inputBean.getClassPath());
		configBean.setClassPath(classPath);
		
		configBean.setRecurse(inputBean.isRecurse());
		configBean.setListener(inputBean.getListener());
		
		// cleanup paths if necessary
		cleanup(configBean);
		
		// To do, clean logging up
		if(inputBean.getVerboseLevel()>0) {
		    Log.setVerboseLevel(Verbose.LOW);
		}
		
    	ClassLoader parentLoader = this.getClass().getClassLoader();
    	if(configBean.getClassPath()!=null) {
    		
    		/*
    	    ArrayList<URL> urlList = configBean.getClassPath();
			for (int n = 0; n < urlList.size(); n++) {
				URL url = urlList.get(n);
				// remove "jar:file" prefix, replace with "file:"
				try {
					String urlStr = url.toURI().toASCIIString();
					if (urlStr.startsWith("jar:file:")) {
						urlStr.replaceFirst("jar:file:", "file:");
					}
					urlList.set(n, new URL(urlStr));
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			} */  
        	URL[] urls = new URL[configBean.getClassPath().size()];
    	    urls = (URL[])configBean.getClassPath().toArray(urls);
    	    
    	    // for loading class files
        	URLClassLoader loader = new URLClassLoader(urls, parentLoader);	
        	configBean.setClassLoader(loader);
        	
        	// for loading threadpolicy.xml
        	URLClassLoader isolatedLoader = new URLClassLoader(urls);
        	configBean.setIsolatedClassLoader(isolatedLoader);
    	} else {
		   configBean.setClassLoader(parentLoader);
		   configBean.setIsolatedClassLoader(parentLoader);
    	}
	}
	
	/**
	 * Return errors from static analysis
	 */
	public ArrayList<ICheckThreadError> getErrors() {
		return fErrorList;
	}
	
	/** 
	 * Perform CheckThread static analysis
	 */
	public void run() throws CheckThreadException {
		try {
			runHelper();
		} catch (Exception e) {
			e.printStackTrace();
			Log.severe(e.toString());
            throw new CheckThreadException(e);		
		}
	}
	
	// helper
	private void runHelper() {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		ArrayList<URI> pathList = configBean.getTargetPath();
		Log.toolInfo("***START CHECKTHREAD***");
		configBean.debugPrettyPrintPath();
		
		// clear out cache
		fCacheClassFiles.clear();
		
		// loop through entries
		for (URI path : pathList) {
			File file = new File(path);
			
			// jar file
			if (file.isFile() && file.getName().endsWith("*.jar")) {
				//analyzeJar(file);
				
			// class file
			} else if (file.isFile()) {
				analyzeClassFile(file);
				
			// directory containing class files
			} else if (file.isDirectory()) {
				analyzeDir(file,configBean.isRecurse());
		
		    // error
			} else {
				Log.reportError("ERROR, invalid file: " + file.toString());
			}
			
			fErrorList.addAll(LockAdjacencyListManager.getErrors());
		}
	}
	
	// helper, analyze a class file
	private void analyzeClassFile(File classFile) {
		ArrayList<File> classFileList = FileUtil.getEnclosingClassesForPrimaryClass(classFile);
		if (classFileList != null && classFileList.size() > 0) {
			analyzeClassFileList(classFileList);
		}
	}
	
	// helper, analyze is directory
	private void analyzeDir(File fileDir, boolean isRecurse) {
		
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		ICheckThreadListener listener = configBean.getListener();
		ArrayList<File> classFileList = FileUtil.loadClassFilesForDir(fileDir);

		if (classFileList != null && classFileList.size() > 0) {
			// notify listener
			String msg = "Analyzing " + classFileList.size() + " class files in " + fileDir;
			if(listener!=null) {
				CheckThreadUpdateEvent evt = new CheckThreadUpdateEvent(msg);
				listener.analyzeUpdate(evt);
			}
			analyzeClassFileList(classFileList);
		}

		// get all sub-directories
		if (isRecurse) {
            File[] dirList = FileUtil.getSubDirectoriesForFile(fileDir);
			for (File dir : dirList) {
				analyzeDir(dir, isRecurse);
			}
		}
	}

	// not tested yet
	/*private void analyzeJar(File file) {
		ParseHandler handler = new ParseHandler();
		HashMap<String,AccessibleObject> mapSynthetic = new HashMap<String,AccessibleObject>();
		try {
		   JarFile jarFile = new JarFile(file);	
		   JarEntry[] sortedJarEntries = JarUtil.getSortedJarEntries(jarFile);
		   for(JarEntry jarEntry : sortedJarEntries) {
			   if (jarEntry.getName().endsWith(".class")) {
		            IClassFileParser parser = ClassFileParserFactory.getClassFileParser(mapSynthetic);
		            parser.addHandler(handler);
		            parser.parseZipFile(jarFile.getName(), jarEntry.getName());
		            ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();  
		            for(ICheckThreadError bean : list) {
                        processError(bean);
		            }   
			   }
		   }	   
		} catch(IOException e) {
			Log.reportException(e);
		}
	}*/

	// Analyze a list of class files
	private boolean analyzeClassFileList(ArrayList<File> classFiles) {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		ParseHandler handler = new ParseHandler();
		boolean noErrors = true;
		
		// cache synthetic methods, needed for inner classes
		HashMap<String,AccessibleObject> mapSynthetic = new HashMap<String,AccessibleObject>();
		
		// loop through class files
		for (File classFile : classFiles) {

			String key = classFile.getAbsoluteFile().toURI().toString();
			
			// If we haven't analyzed this class file yet
			if (fCacheClassFiles.get(key) == null) {
                
				Log.debugInfo("Analyzing: " + classFile);

				IClassFileParser parser = ClassFileParserFactory
						.getClassFileParser(mapSynthetic);
				parser.addHandler(handler);
				try {
					configBean.setFoundClassFile(true);
				    parser.parseClassFile(classFile);
				} catch(Exception e) {
					Log.reportError("Exception in CheckThreadMain");
					Log.reportException(e);
				} 
				ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();
				for (ICheckThreadError bean : list) {
					noErrors = false;
					processError(bean);
				}
				handler.clear();
				
				fCacheClassFiles.put(key, Boolean.TRUE);
			}
		}
		return noErrors;
	}
	
	// helper add to error list
	private void processError(ICheckThreadError bean) {
	    bean.printErr();
	    fErrorList.add(bean);
	}
	
	/**
	 * Returns true if at least one annotation was found
	 * in the anlayzed class files
	 */
	public boolean foundCheckThreadAnnotations() {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		return configBean.getFoundCheckThreadAnnotations();
	}
	
	/**
	 * Return true if there is at least one class file
	 * to analyze
	 */
	public boolean foundClassFileToAnalyze() {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		return configBean.getFoundClassFile();	
	}
	
	// helper
	private static void cleanup(ConfigBean configBean) {
		ArrayList<URL> classPath = configBean.getClassPath();
		ArrayList<URI> targetPath = configBean.getTargetPath();

		// add target paths to classpath if they are not on there already
		for (URI uri : targetPath) {
			try {
				URL url = uri.toURL();
				if (!classPath.contains(url)) {
					classPath.add(url);
				}
			} catch (Exception e) {
			}
		}
	}
	
	// For testing
	public static void main(String [] args) throws Exception {

		String classPathStr = "C:/project/checkthread/checkthread-1.0/class_eclipse";
		ClassPath classPath = new ClassPath(classPathStr);
		ClassPath.ClassFile classFile = classPath.getClassFile("NoPackageClass");
		Log.debugInfo("ClassFile: " + classFile.getPath());		
		ClassParser classParser = new ClassParser(classFile.getPath());
		JavaClass javaClass = classParser.parse();
		Method[] methods = javaClass.getMethods();
		for(Method m : methods) {
			Log.debugInfo("Method : " + m.getName());
			Attribute[] attributes = m.getAttributes();
			for(Attribute a : attributes) {
				Log.debugInfo("a: " + a.toString());	
			}
		}
	}
}
