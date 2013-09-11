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

package org.checkthread.config;

import org.checkthread.main.*;

import java.net.*;
import java.util.ArrayList;

/**
 * Bean value object holding configuration information used
 * during CheckThread processing.
 */
public class ConfigBean {

	private ArrayList<URI> fTargetPath = new ArrayList<URI>();
	private ArrayList<URL> fClassPath = new ArrayList<URL>();
	private Verbose fVerbose = Verbose.NONE;
    private ClassLoader fClassLoader;
    private ClassLoader fIsolatedClassLoader;
    private boolean fIsRecurse = true;
    private ICheckThreadListener fCheckThreadListener = null;
    private boolean fFoundCheckThreadAnnotations = false;
    private boolean fFoundClassFile = false;
    
    private ConfigBean() {}
    
    static ConfigBean newInstance() {
    	return new ConfigBean();
    }
    
    public boolean getFoundCheckThreadAnnotations() {
    	return fFoundCheckThreadAnnotations;
    }
    
    public void setFoundCheckThreadAnnotations(boolean b) {
    	fFoundCheckThreadAnnotations = b;
    }
    
    public void setFoundClassFile(boolean b) {
    	fFoundClassFile = b;
    }
    
    public boolean getFoundClassFile() {
    	return fFoundClassFile;
    }
    
    public void setListener(ICheckThreadListener l) {
    	fCheckThreadListener = l;
    }
    
    public ICheckThreadListener getListener() {
    	return fCheckThreadListener;
    }
    
    public void setTargetPath(ArrayList<URI>  targetPath) {
 	   fTargetPath = targetPath;
    }
    
    public ArrayList<URI>  getTargetPath() {
 	   return fTargetPath;
    }
    
    public void setClassPath(ArrayList<URL>  targetPath) {
 	   fClassPath = targetPath;
    }
    
    public ArrayList<URL>  getClassPath() {
 	   return fClassPath;
    }
    
    public void setRecurse(boolean b) {fIsRecurse = b;}
    
    public boolean isRecurse() {return fIsRecurse;}
    
	public void setVerboseLevel(Verbose v) {
		fVerbose = v;
	}

	public Verbose getVerboseLevel() {
		return fVerbose;
	}
	
	public void setClassLoader(ClassLoader loader) {
		fClassLoader = loader;
	}
	
	public ClassLoader getClassLoader() {
		return fClassLoader;
	}
	
	public void setIsolatedClassLoader(ClassLoader loader) {
		fIsolatedClassLoader = loader;
	}
	public ClassLoader getIsolatedClassLoader() {
		return this.fIsolatedClassLoader;
	}
	
	public void debugPrettyPrintPath() {
		ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
		ArrayList<URI> pathList = configBean.getTargetPath();
		
		for(URI path : pathList) {
			Log.toolInfo("Target Directory: " + path);
		}
		
		ArrayList<URL> classPathList = configBean.getClassPath();
		for(URL path : classPathList) {
			Log.toolInfo("Class Path: " + path);
		}
		
	}
	
}
