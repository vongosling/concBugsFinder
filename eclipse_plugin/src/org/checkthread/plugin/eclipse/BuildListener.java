/*
Copyright (c) 2008-2009 Joe Conti CheckThread.org

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

package org.checkthread.plugin.eclipse;

import java.io.File;
import java.net.URI;
import java.util.logging.*;
import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.*;

/**
 * @deprecated
 * This was originally used for running CheckThread automatically whenever
 * a build occurred. Having an explicit toolbar button to run CheckThread 
 * seems like a better approach.
 */
public class BuildListener implements IResourceChangeListener {
	
	private boolean fIsEnabled = false;
	private static Logger sLogger = Logger.getLogger(BuildListener.class.getName());
    public static boolean IsErrorDialog = true;
	
	public void setEnabled(boolean b) {fIsEnabled = b;}
	
	public boolean getEnabled() {return fIsEnabled;}
	
	public BuildListener() {}
	
	/**
	 * This fires when the user builds their Java project
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("resourceChanged: " + Thread.currentThread().toString());
		if (fIsEnabled) {
			int level = 0;
			
			// find out which projects were just built
			IResourceDelta delta = event.getDelta();
			ArrayList<IProject> projectList = new ArrayList<IProject>();
			processDeltaRecurse(level, delta, projectList);
			
			// run CheckThread on projects
			//XCheckThreadRunner c = XCheckThreadRunner.newInstance();
			//c.initProject(projectList);
			
			//sLogger.info("Project list: " + projectList.size());			
		}
	}

	// find out which class files were just built
	private void processDeltaRecurse(int level, 
			                         IResourceDelta delta,
			                         ArrayList<IProject> projectList) {
		level++;
		IResourceDelta[] kids = delta.getAffectedChildren();
		for (IResourceDelta delta2 : kids) {
			IResource res = delta2.getResource();
			if (res.isDerived()) {
				URI uri = res.getLocationURI();
				File file = new File(uri);
				if (file!=null && file.isFile()) {
					IProject project = res.getProject();
					if(!projectList.contains(project)) {
						projectList.add(project);
					}
				}
			}
			processDeltaRecurse(level, delta2, projectList);
		}	
	}
}
