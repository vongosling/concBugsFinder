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

package org.checkthread.plugin.eclipse.consoleview;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import org.checkthread.annotations.ThreadConfined;
import org.checkthread.annotations.ThreadName;

@ThreadConfined(ThreadName.SWT_UI)
public class ConsoleViewManager {
	
	private final static String CONSOLE_NAME = "CheckThread";
	private final static Logger sLogger = Logger.getLogger(ConsoleViewManager.class.getName());
	private static MessageConsole sConsole = null;
	private static MessageConsoleStream sConsoleStream = null;
	
	public static final int MSG_INFORMATION = 1;
	public static final int MSG_ERROR = 2;
	public static final int MSG_WARNING = 3;
	
	public synchronized static void init() {
		sLogger.info("CONSOLE: init console");
		MessageConsole mConsole = newConsole();
		//mConsole.activate();
		sConsole = mConsole;
		sConsoleStream = sConsole.newMessageStream();
	}

	public static synchronized void printToConsole(final String msg) {
		printToConsole(msg,MSG_INFORMATION);
	}
	
	public static synchronized void printToConsole(final String msg,
			final int msgKind) {
		sLogger.info("CONSOLE: pre-printToConsole");
		if (sConsole != null && sConsoleStream != null) {
			sLogger.info("CONSOLE: printToConsole: " + msg);
			try {
				sConsoleStream.println(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void setColor(int msgKind)
	{		
		int swtColorId = SWT.COLOR_DARK_GREEN;
		
		switch (msgKind)
		{
			case MSG_INFORMATION:
				swtColorId = SWT.COLOR_DARK_GREEN;				
				break;
			case MSG_ERROR:
				swtColorId = SWT.COLOR_DARK_MAGENTA;
				break;
			case MSG_WARNING:
				swtColorId = SWT.COLOR_DARK_BLUE;
				break;
			default:				
		}	
		
		sConsoleStream.setColor(Display.getCurrent().getSystemColor(swtColorId));
	}
	
	// Ref:
	// http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
	private static synchronized MessageConsole newConsole() {
		MessageConsole mConsole = null;
		
		// Remove all pre-existing consoles with same name
		// This effectivley clears out the console
		// Using MessageConsole.clearConsole() is too buggy and asynchronous
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (CONSOLE_NAME.equals(existing[i].getName())) {
				mConsole = (MessageConsole) existing[i];
				sLogger.info("CONSOLE: found pre-existing console");
				conMan.removeConsoles(new IConsole[] { mConsole });
			}
		}

		// create a new one
		sLogger.info("CONSOLE: Creating new console");
		mConsole = new MessageConsole(CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { mConsole });
		return mConsole;
	}
}
