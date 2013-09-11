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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.checkthread.annotations.*;
import org.checkthread.main.CheckThreadException;
import org.checkthread.main.CheckThreadMainFactory;
import org.checkthread.main.CheckThreadUpdateEvent;
import org.checkthread.main.ICheckThreadListener;
import org.checkthread.main.ICheckThreadMain;
import org.checkthread.main.InputBean;
import org.checkthread.plugin.eclipse.consoleview.ConsoleViewManager;
import org.checkthread.plugin.eclipse.resources.ResourceNames;
import org.checkthread.plugin.eclipse.errorview.ErrorViewManager;

/**
 * Main entry point for the CheckThread static analysis engine
 */
@NotThreadSafe
final public class CheckThreadRunner {
	private static Logger sLogger = Logger.getLogger(CheckThreadRunner.class.getName());
	
	public static void clear() {
		ErrorViewManager.clearAllMarkers();
	}

	/**
	 * Runs once per click on the CheckThread button
	 */
	public static void init() {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject[] projectList = workspace.getRoot().getProjects();
		
		ConsoleViewManager.init();
		ConsoleViewManager.printToConsole("*** CheckThread Eclipse Plugin (1.0.9 beta) ***");
		ConsoleViewManager.printToConsole(" ");
		
		// run on background thread
		Util.submit(new Runnable() {
			public void run() {
				boolean foundProject = false;
				for (IProject project : projectList) {
					if(project.isOpen() 
					   && project.isAccessible()
					   && project.exists()) {
					ErrorViewManager.clearMarkers();
					checkProject(project);
					foundProject = true;
					}
				}	
				
				if(!foundProject) {
					ConsoleViewManager.printToConsole("CheckThread found no open projects to analyze.");	
				}
			}
		});
	}
	
    // helper for analyzing a project
	private static void checkProject(IProject project) {

		HashMap<IProject, Boolean> projectSearchMap = new HashMap<IProject, Boolean>();
		ArrayList<URI> classPathList = new ArrayList<URI>();
		ArrayList<IPath> srcDirList = new ArrayList<IPath>();
		ArrayList<URI> targetFileList = new ArrayList<URI>();

		loadDirFromProject(projectSearchMap,
				false,
				project, 
				srcDirList, 
				targetFileList, 
				classPathList);
	
		ConsoleViewManager.printToConsole("CHECKTHREAD ANALYZING: " + project.getName());
		
		if(targetFileList.size()<1) {
			ConsoleViewManager.printToConsole(ResourceNames.NO_TARGET_FOUND_MSG);
		} else {
		    invokeCheckThreadAnalyzer(srcDirList, 
		    		targetFileList, 
		    		classPathList,
		    		project);
		}
	}

	// helper for analyzing
	private static void invokeCheckThreadAnalyzer(ArrayList<IPath> srcDirList,
			ArrayList<URI> targetFileList, 
			ArrayList<URI> classPathList,
			IProject project) {
		
        // Output target
        for (URI uri : targetFileList) {
        	ConsoleViewManager.printToConsole("Target Path: " + uri);
        }
        
		// Configure CheckThread
		InputBean inputBean = InputBean.newInstance();
		inputBean.setVerboseLevel(0);
		inputBean.setListener(new ICheckThreadListener() {
			public void analyzeUpdate(CheckThreadUpdateEvent evt) {
				if(evt.getType()==CheckThreadUpdateEvent.Type.PROGRESS)
				{	
					ConsoleViewManager.printToConsole(evt.getMessage());						
				}
			}
		});
		inputBean.setTargetPath(targetFileList);
		inputBean.setClassPath(classPathList);
		runCheckThread(inputBean, project, srcDirList);
	}

	// runs on main thread
	private static void runCheckThread(final InputBean inputBean,
			final IProject project, final ArrayList<IPath> srcDirList) {
		// run CheckThread
		ICheckThreadMain checkThread = CheckThreadMainFactory.newInstance(inputBean);

		try {
			long startTime = System.currentTimeMillis();
			checkThread.run();
			long stopTime = System.currentTimeMillis();
			final long runTime = stopTime - startTime;
			ConsoleViewManager.printToConsole("");
			ConsoleViewManager.printToConsole("CHECKTHREAD FINISHED ANALYZING: " + project.getName());
			ConsoleViewManager.printToConsole("Total analyze time: " + runTime + " ms.");

		} catch (CheckThreadException e) {
			String msg = e.getErrorMessageForClientName("Eclipe");
			ConsoleViewManager.printToConsole(msg);
			return;
		}
		ErrorViewManager.reportCheckThreadErrors(checkThread, project,srcDirList);
	}
	
	// runs on worker thread
	private static void loadDirFromProject(
			HashMap<IProject, Boolean> projectSearchMap,
			boolean isrecursed, IProject project,
			ArrayList<IPath> srcDirList, ArrayList<URI> targetFileList,
			ArrayList<URI> classPathList) {

		// if we already searched this project
		if (projectSearchMap.get(project) != null) {
			return;
		
		// we haven't searched this project yet
		} else {
			// add to cache
			projectSearchMap.put(project, true);
		}

		// recursive traverse referenced projects
		// stopping condition: project already searched
		try {
			IProject[] projectList = project.getReferencedProjects();
			for (IProject p : projectList) {
				loadDirFromProject(projectSearchMap,
						true,
						p, 
						srcDirList, 
						targetFileList, 
						classPathList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IJavaProject javaProject = JavaCore.create(project);
		IPath defaultOutputLocationRelative = null;

		try {
			defaultOutputLocationRelative = javaProject.getOutputLocation();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		sLogger.info("DEFAULT OUTPUT LOCATION RELATIVE: "
				+ defaultOutputLocationRelative);

		IPath projectLocationAbsolute = project.getLocation();
		sLogger.info("PROJECT LOCATION: " + projectLocationAbsolute);

		// Make path absolute
		IPath defaultOutputLocationAbsolute = projectLocationAbsolute
				.append(defaultOutputLocationRelative.removeFirstSegments(1));
		sLogger.info("DEFAULT OUTPUT LOCATION ABSOLUTE: "
				+ defaultOutputLocationAbsolute);

		// Work around, stomp over target java files. Instead, just give the
		// root directory
		sLogger
				.info("WORKAROUND: IGNORE CHANGED CLASS FILES< RECHECK EVERYTHING");
		
		if(!isrecursed) {
			URI uri = defaultOutputLocationAbsolute.toFile().toURI();
			if(uri!=null) {
		       targetFileList.add(uri);
			}
		}
		
		// Add to input
		URI cURI = defaultOutputLocationAbsolute.toFile().toURI();
		if(cURI!=null) {
		    classPathList.add(cURI);
		}
		
		// Loop through classpath entries and get src directory list
		IClasspathEntry[] rawClassPathList = null;
		try {
			rawClassPathList = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		if (rawClassPathList != null) {
			for (IClasspathEntry classPathEntry : rawClassPathList) {
				switch (classPathEntry.getEntryKind()) {

				// Source Directory
				case IClasspathEntry.CPE_SOURCE: {
					if(!isrecursed) {
					    IPath p = classPathEntry.getPath().removeFirstSegments(1);
					    if(p!=null) {
					    	srcDirList.add(p);
					    	sLogger.info("CPE_SOURCE: " + p);
					    }			    
					}
					break;
				}

					// external libraries used
				case IClasspathEntry.CPE_LIBRARY: {

					File file = classPathEntry.getPath().toFile();
					IPath p;
					// The entry may be a relative path to the project root
					// or it could be an absolute path to a library.
					if (file.isFile() || file.isDirectory()) {
						p = classPathEntry.getPath();
					} else {
						p = projectLocationAbsolute.append(classPathEntry
								.getPath().removeFirstSegments(1));
					}

					URI uri = p.toFile().toURI();
					if(uri!=null) {
					    classPathList.add(uri);
					}
					sLogger.info("CPE_LIBRARY: " + uri);
					break;
				}

					// ignore
				case IClasspathEntry.CPE_CONTAINER:
					sLogger.info("CPE_CONTAINER: " + classPathEntry);
					break;

				//ignore
				case IClasspathEntry.CPE_PROJECT:
					sLogger.info("CPE_PROJECT: " + classPathEntry);
					break;
				}
			}
		}
	}

}
