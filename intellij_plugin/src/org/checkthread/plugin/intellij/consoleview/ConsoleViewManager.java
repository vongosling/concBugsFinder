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

package org.checkthread.plugin.intellij.consoleview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.TextConsoleBuilder;

import javax.swing.*;
import java.util.HashMap;

import org.checkthread.plugin.intellij.Util;

/**
 * Manages console output view
 */
public class ConsoleViewManager {

    final private static Logger sLogger = Logger.getInstance(ConsoleViewManager.class.getName());
    final private static HashMap<Project,ConsoleInfo> fConsoleMap = new HashMap<Project,ConsoleInfo>();
    final private static String ID = "CheckThread Output";

    // helper data structure for mapping view and window
    private static class ConsoleInfo {
        ConsoleView consoleView;
        ToolWindow toolWindow;
    }

    /**
     * Clears out console window
     */
    public static void clear() {
        Project project = Util.getCurrentProject();
        if(project!=null) {
            ConsoleInfo consoleInfo = fConsoleMap.get(project);
            if(consoleInfo!=null) {
                if(consoleInfo.toolWindow!=null && consoleInfo.consoleView!=null) {
                    consoleInfo.consoleView.clear();
                }
            }
        }
    }

    /**
     * Initialize console view
     * Call this once at the beginning of an analysis
     * @param project
     */
    public synchronized static void init(Project project) {
        if(project!=null) {
            ConsoleInfo consoleInfo = fConsoleMap.get(project);
            if(consoleInfo!=null && consoleInfo.toolWindow!=null
                    && !consoleInfo.toolWindow.isDisposed()
                    && consoleInfo.consoleView!=null)
            {
                consoleInfo.consoleView.clear();
            } else {
                consoleInfo = createNewConsoleForProject(project);
                fConsoleMap.put(project,consoleInfo);
            }
            if(consoleInfo!=null && consoleInfo.toolWindow!=null) {
                consoleInfo.toolWindow.show(null);
            }
        }
    }

    /**
     * Prints the input string to the console window
     * @param project
     * @param msg
     */
    public static void printToConsole(final Project project, final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ConsoleInfo consoleInfo = fConsoleMap.get(project);
                if(consoleInfo!=null && consoleInfo.consoleView!=null) {
                    consoleInfo.consoleView.print(msg + '\n', ConsoleViewContentType.NORMAL_OUTPUT);
                }
            }
        });
    }
    
    // create new console helper
    private static ConsoleInfo createNewConsoleForProject(Project project) {

        ConsoleInfo consoleInfo = new ConsoleInfo();

        TextConsoleBuilderFactory textConsoleBuilderFactory = TextConsoleBuilderFactory.getInstance();
        TextConsoleBuilder consoleBuilder = textConsoleBuilderFactory.createBuilder(project);
        consoleInfo.consoleView = consoleBuilder.getConsole();
        ToolWindowManager manager = ToolWindowManager.getInstance(project);


        consoleInfo.toolWindow = manager.getToolWindow(ID);
        if(consoleInfo.toolWindow ==null) {
            consoleInfo.toolWindow = manager.registerToolWindow(ID,
                    consoleInfo.consoleView.getComponent(),
                    ToolWindowAnchor.BOTTOM);
        }
        return consoleInfo;
    }



}
