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

import java.util.logging.Logger;

import org.eclipse.ui.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.checkthread.annotations.*;
import org.checkthread.plugin.eclipse.errorview.*;

@ThreadConfined(ThreadName.SWT_UI)
public class CheckThreadToggleAction implements IWorkbenchWindowActionDelegate, IWorkbenchListener  
{
	private static Logger sLogger = Logger.getLogger(CheckThreadToggleAction.class.getName());
    
	public boolean preShutdown(IWorkbench workBench, boolean forced) {
		ErrorViewManager.clearAllMarkers();
		return true;	
	}

	public void postShutdown(IWorkbench workBench) {}
	
	// This runs every time the user clicks on the toggle button
	public void run(IAction proxyAction) 
	{
		sLogger.info("run");
		if(proxyAction.isChecked()) {
			CheckThreadRunner.init();			
		} else {
			CheckThreadRunner.clear();
		}
	}
	
	// This runs once per workspace session
	public void init(IWorkbenchWindow window) 
	{
		Util.setDisplay(window.getWorkbench().getDisplay());
		Util.setShell(window.getShell());
		window.getWorkbench().addWorkbenchListener(this);
	}

	public void selectionChanged(IAction proxyAction, ISelection selection) 
	{}
	
	public void dispose() {
		CheckThreadRunner.clear();
	}
}
