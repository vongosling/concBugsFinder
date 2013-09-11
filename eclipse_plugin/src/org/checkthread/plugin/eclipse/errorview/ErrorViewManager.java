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

package org.checkthread.plugin.eclipse.errorview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.checkthread.annotations.*;
import org.checkthread.main.ICheckThreadError;
import org.checkthread.main.ICheckThreadMain;
import org.checkthread.plugin.eclipse.Util;
import org.checkthread.plugin.eclipse.consoleview.ConsoleViewManager;
import org.checkthread.plugin.eclipse.resources.ResourceNames;

@ThreadConfined(ThreadName.SWT_UI)
final public class ErrorViewManager {
	
	private static Logger sLogger = Logger.getLogger(ErrorViewManager.class.getName());
	private static ArrayList<IMarker> sErrorMarkerCache = new ArrayList<IMarker>(); 
	
	@ThreadSafe
	public static void reportCheckThreadErrors(
			final ICheckThreadMain checkThread,
			final IProject project, 
			final ArrayList<IPath> srcDirList) {
		
		// REPORT ERRORS ON SWT THREAD
		Util.invokeOnSWT(new Runnable() {
			
			@ThreadConfined(ThreadName.SWT_UI)
			public void run() {
				
				// Process Results
				ArrayList<ICheckThreadError> errorList = checkThread.getErrors();

				// loop through errors
				for (final ICheckThreadError error : errorList) {
					final IFile file = loadSrcFileFromProject(project,
							srcDirList, error.getSourceFile());
					if (file != null && file.exists()) {
						sLogger.info("File: " + file);
						createMarker(file, error);
					}
				}
				
		        if (errorList.size() < 1) {
		            if (!checkThread.foundClassFileToAnalyze()) {
		            	ConsoleViewManager.printToConsole(ResourceNames.NO_CLASS_FILES_MSG);
		            } else if (!checkThread.foundCheckThreadAnnotations()) {
		            	ConsoleViewManager.printToConsole(ResourceNames.NO_CHECKTHREAD_ANNOTATIONS_FOUND_MSG);
		            } else {
		            	ConsoleViewManager.printToConsole(ResourceNames.NO_ERRORS_FOUND_MSG);
		            }
		        } else {
		        	ConsoleViewManager.printToConsole("CheckThread found " + errorList.size() + " errors. Open the 'Problems' window for error listing.");
		        }
			}
		});

	}
	
	@ThreadConfined(ThreadName.SWT_UI)
	private static IFile loadSrcFileFromProject(IProject project,
			ArrayList<IPath> srcDirList, String fileName) {
		IFile iFile;
		IFile retval = null;
		for (IPath dir : srcDirList) {
			IPath newPath = dir.append(fileName);
			sLogger.info("Trying: " + newPath.toFile());
			String fileStr = newPath.toOSString(); // directory should use "/" not "\"
			iFile = project.getFile(fileStr);
			if (iFile.exists()) {
				sLogger.info("Found Src: " + iFile);
				retval = iFile;
				break;
			}
		}
		return retval;
	}

	@ThreadConfined(ThreadName.SWT_UI)
	// ThreadConfined - dispatching this method on other threads causes 
	// problems and the eclipse plugin doesn't refresh
	private static void createMarker(IFile iFile, ICheckThreadError error) {
		sLogger.info("createMarker: " + Thread.currentThread().toString());
		sLogger.info("createMarker: " + iFile + " line: "
				+ error.getLineNumber());

		try {
			int line = error.getLineNumber();
			IMarker m = iFile.createMarker(IMarker.PROBLEM);
			m.setAttribute(IMarker.LINE_NUMBER, new Integer(line));
			m.setAttribute(IMarker.MESSAGE, error.getErrorMessage());
			m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			m.setAttribute(IMarker.LOCATION, "N/A");
			m.setAttribute(IMarker.TRANSIENT,true);
			sErrorMarkerCache.add(m);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@ThreadSafe
	public static void clearMarkers() {
		
		sLogger.info("clearMarkers");
		
		Util.invokeOnSWT(new Runnable() {
			
			@ThreadConfined(ThreadName.SWT_UI)
			public void run() {
				try {
					Iterator<IMarker> it = sErrorMarkerCache.iterator();
					while(it.hasNext()) {
						IMarker marker = it.next();
						if(marker!=null) {
							marker.delete();
						}	
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@ThreadSafe
	public static void clearAllMarkers() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject[] projects = workspace.getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isOpen()) {
				clearMarkers();
			}
		}
	}
}
