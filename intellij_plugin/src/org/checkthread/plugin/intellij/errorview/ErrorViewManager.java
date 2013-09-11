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

package org.checkthread.plugin.intellij.errorview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.util.ui.ErrorTreeView;
import com.intellij.peer.PeerFactory;
import com.intellij.ui.errorView.ErrorViewFactory;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

import org.checkthread.plugin.intellij.Util;

/**
 * This classes manages the life cycle of error markers
 */
final public class ErrorViewManager {
    
    final private static Logger sLogger = Logger.getInstance(ErrorViewManager.class.getName());
    final private static HashMap<Project, ErrorViewInfo> fErrorMap = new HashMap<Project, ErrorViewInfo>();
    final private static Vector<ErrorMarker> sErrorMarkerCache = new Vector<ErrorMarker>();
    final private static String WINDOW_ID = "CheckThread Error List";

    // inner helper class
    private static class ErrorViewInfo {
        ErrorTreeView errorTreeView;
        ToolWindow toolWindow;
    }

    /**
     * Add error marker
     * @param errorMarker
     */
    public static void addErrorMarker(ErrorMarker errorMarker) {
        sErrorMarkerCache.add(errorMarker);
    }
    
    /**
     * Clear out all error decorations
     */
    public static void clear() {
         clearAllErrorMarkers();
         clearPreviousView(Util.getCurrentProject());
    }

    /**
     * clear previous error tree
     * @param project
     */
    public synchronized static void clearPreviousView(Project project) {
        if(project!=null) {
            ErrorViewInfo errorViewInfo = fErrorMap.get(project);

            // Remove previous error tree view
            if(errorViewInfo!=null)
            {
                ToolWindowManager manager = ToolWindowManager.getInstance(project);
                manager.unregisterToolWindow(WINDOW_ID);
                if(errorViewInfo.errorTreeView!=null) {
                    errorViewInfo.errorTreeView.dispose();
                }
            }
            fErrorMap.remove(project);
        }
    }

    /**
     * get error tree view
     * @param project
     * @return
     */
    public synchronized static ErrorTreeView getErrorTreeView(Project project) {
        ErrorTreeView retval = null;
        ErrorViewInfo errorViewInfo = fErrorMap.get(project);

        // return tree if viable
        if(errorViewInfo!=null)
        {
            retval =  errorViewInfo.errorTreeView;
        } else {
            retval = null;
        }

        return retval;
    }

    /**
     * Create new error view
     * @param project
     */
    public static void newErrorView(Project project) {
        if(project!=null) {
            ErrorViewInfo errorViewInfo = fErrorMap.get(project);
            if(errorViewInfo!=null) {
                clearPreviousView(project);
            }
            errorViewInfo = createErrorView(project);
            fErrorMap.put(project,errorViewInfo);
        }
    }

    // helper
    private static ErrorViewInfo createErrorView(Project project) {

        // create tree view
        ErrorViewInfo errorViewInfo = new ErrorViewInfo();
        PeerFactory peerFactory = PeerFactory.getInstance();
        ErrorViewFactory viewFactory = peerFactory.getErrorViewFactory();
        ErrorTreeView errorTreeView = viewFactory.createErrorTreeView(project,
                "CheckThread",
                false,null,null,null);
        errorViewInfo.errorTreeView = errorTreeView;

        // create tool window
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        errorViewInfo.toolWindow = manager.getToolWindow(WINDOW_ID);

        // create new tool window if necessary
        if(errorViewInfo.toolWindow == null
            || errorViewInfo.toolWindow.isDisposed())
        {
            errorViewInfo.toolWindow  =  manager.registerToolWindow(WINDOW_ID,
                    errorViewInfo.errorTreeView.getComponent(),
                    ToolWindowAnchor.BOTTOM);
        }
        return errorViewInfo;
    }

    /**
     * Clear all the markers for a given module/project
     * @param m
     */
    public static void clearPreviousMarkups(Module m) {
        System.out.println("clearMarkups: " + Thread.currentThread());
        Project project = m.getProject();
        Iterator<ErrorMarker> iterator = sErrorMarkerCache.iterator();
        while (iterator.hasNext()) {
            ErrorMarker errorMarker = iterator.next();
            if (errorMarker.Project == project) {
                errorMarker.MarkupModel.removeAllHighlighters();
            }
        }
        sErrorMarkerCache.clear();
        EditorFactory.getInstance().refreshAllEditors();
    }

    private static void clearAllErrorMarkers() {
        for (ErrorMarker errorMarker : sErrorMarkerCache) {
            errorMarker.MarkupModel.removeAllHighlighters();
        }
    }
}
