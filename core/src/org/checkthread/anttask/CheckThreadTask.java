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

package org.checkthread.anttask;

import java.util.*;
import java.io.File;
import java.net.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import org.checkthread.main.*;
import org.checkthread.config.*;

/*
 * Ant task for CheckThread
 */
public class CheckThreadTask extends Task {
    private Path fClassPath;
    private Path fTargetPath;
    private String fVerbose = "";

    public void init() {}
    
    // The method executing the task
	public void execute() throws BuildException {	
		
		// print out target path
		if (fTargetPath != null) {
			String[] path = fTargetPath.list();
			for (String p : path) {
				Log.toolInfo("Target path entry: " + p);
			}
		} else {
			throw new BuildException("ERROR: targetpath not specified");
		}

		// print out class path
		if (fClassPath != null) {
			String[] path = fClassPath.list();
			for (String p : path) {
				Log.toolInfo("Class path entry: " + p);
			}
		} else {
			throw new BuildException("ERROR: classpath not specified");
		}
		
		ArrayList<URI> targetList = convert(fTargetPath.list());
		ArrayList<URI> classList = convert(fClassPath.list());
		
		// CheckThread inputs
		InputBean inputBean = InputBean.newInstance();
		inputBean.setTargetPath(targetList);
		inputBean.setClassPath(classList);
		if(fVerbose.equals("true")) {
	        inputBean.setVerboseLevel(1);
		}
	    
		// Run CheckThread
		ICheckThreadMain checkThread = CheckThreadMainFactory.newInstance(inputBean);
		
		try {
			checkThread.run();
		} catch (CheckThreadException e) {
			throw new BuildException(e.getErrorMessageForClientName("ant"));
		}
		
		// Get errors, if any
		ArrayList<ICheckThreadError> errList = checkThread.getErrors();
		
		// Throw exception if errors
		if(errList.size()>0) {
			throw new BuildException("CheckThread Errors");
		}
		
	}
    
	public void setVerbose(String v) {
		fVerbose = v;
	}
	
	public String getVerbose() {
		return fVerbose;
	}
	
    public void setClasspath(Path classpath) {
        if (fClassPath == null) {
        	fClassPath = classpath;
        } else {
        	fClassPath.append(classpath);
        }
    }
   
    public Path getClasspath() {
        return fClassPath;
    }
    
    public Path createClasspath() {
        if (fClassPath == null) {
        	fClassPath = new Path(getProject());
        }
        return fClassPath.createPath();
    }
    
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    public void setTargetpath(Path targetpath) {
        if (fTargetPath == null) {
        	fTargetPath = targetpath;
        } else {
        	fTargetPath.append(targetpath);
        }
    }
   
    public Path getTargetpath() {
        return fTargetPath;
    }
    
    public Path createTargetpath() {
        if (fTargetPath == null) {
        	fTargetPath = new Path(getProject());
        }
        return fTargetPath.createPath();
    }
    
    public void setTargetpathRef(Reference r) {
        createTargetpath().setRefid(r);
    }
    
    private static ArrayList<URI> convert(String[] list) {
        ArrayList<URI> strList = new ArrayList<URI>();
        File file;
        for(String s : list) {
        	file = new File(s);
        	strList.add(file.toURI());
        }
        return strList;
    }
}
