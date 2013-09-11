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

package org.checkthread.plugin.intellij;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.*;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.ErrorTreeView;
import com.intellij.util.ui.MessageCategory;

import org.checkthread.main.*;
import org.checkthread.plugin.intellij.consoleview.ConsoleViewManager;
import org.checkthread.plugin.intellij.errorview.ErrorMarker;
import org.checkthread.plugin.intellij.errorview.ErrorViewManager;
import org.checkthread.plugin.intellij.resources.ResourceNames;

/**
 * Main class for running CheckThread plugin
 * This class will dispatch the work onto a worker thread
 * and then return the results to the Intellij output view
 */
public class CheckThreadRunner {

    final private static Logger sLogger = Logger.getLogger(CheckThreadRunner.class.getName());
    
    /**
     * Main entry point for running CheckThread
     * This runs when the user clicks on the "CheckThread" toolbar button
     * Runs on EDT thread
     */
    //@ThreadConfined(ThreadName.EDT)
    public static void init() {
        ArrayList<URI> targetPath = new ArrayList<URI>();
        ArrayList<URI> classPath = new ArrayList<URI>();
        ArrayList<VirtualFile> srcDirList = new ArrayList<VirtualFile>();

        // Get current project
        final Project project = Util.getCurrentProject();
        ArrayList<Module> moduleList = getModulesForProject(project);

        // init console view
        ConsoleViewManager.init(project);
        ConsoleViewManager.printToConsole(project,ResourceNames.START_MSG);
        ErrorViewManager.clearPreviousView(project);

        if (project == null || moduleList.size()<1) {
            ConsoleViewManager.printToConsole(project, "CheckThread found no main project selected.");
            ConsoleViewManager.printToConsole(project, "Right click on a project node and select 'Set as main project', then run CheckThread.");
        }

        for(Module mod : moduleList) {
            //printConsole("Module name: " + mod.getName() + "is loaded: " + mod.isLoaded());

            ErrorViewManager.clearPreviousMarkups(mod);

            // getCompilerOutputPath method doesn't exist when running
            // on Intellij with JDK 1.5 (e.g. Mac OSX)
            // This is an intellij compatability bug
            VirtualFile output = ModuleRootManager.getInstance(mod).getCompilerOutputPath();
            URI uriOutput = convertVirtualFiletoURI(output);
            if(uriOutput!=null) {
                targetPath.add(uriOutput);
            }

            // GET CLASS PATH
            ModuleRootManager mrm = ModuleRootManager.getInstance(mod);
            OrderEntry[] oeList = mrm.getOrderEntries();
            for (OrderEntry oe : oeList) {

                // Get build class path
                VirtualFile[] vfList = oe.getFiles(OrderRootType.COMPILATION_CLASSES);
                for (VirtualFile oevf : vfList) {
                    URI uriClassPath = convertVirtualFiletoURI(oevf);

                    // remove "jar:file:" prefix, replace with "file:"
                    String uriClassPathStr = uriClassPath.toASCIIString();
                    if(uriClassPathStr.startsWith("jar:file:")) {
                        try {
                            uriClassPath = new URI(uriClassPathStr.replaceFirst("jar:file:","file:"));
                        } catch(URISyntaxException e) {
                            // propogate excetion as unchecked to remove exception clutter
                            throw new IllegalStateException(e);
                        }
                    }

                    if (!classPath.contains(uriClassPath) && uriClassPath!=null) {
                        classPath.add(uriClassPath);
                    }
                }

                // Get root source directories
                VirtualFile[] vfSourceList = oe.getFiles(OrderRootType.SOURCES);
                for (VirtualFile vf : vfSourceList) {
                    if (!srcDirList.contains(vf) && vf!=null) {
                        srcDirList.add(vf);
                    }
                }
            }
        }

        if (targetPath.size() < 1) {
            ConsoleViewManager.printToConsole(project, ResourceNames.NO_TARGET_FOUND_MSG);
        } else {

            // output target to console
            for(URI uri : targetPath) {
                ConsoleViewManager.printToConsole(project, "Target Path: " + uri);
            }

            // RUN CHECKTHREAD
            InputBean inputBean = InputBean.newInstance();
            inputBean.setTargetPath(targetPath);
            inputBean.setClassPath(classPath);
            inputBean.setVerboseLevel(1);
            inputBean.setRecurse(true);
            inputBean.setListener(new ICheckThreadListener() {
                public void analyzeUpdate(CheckThreadUpdateEvent evt) {
                    if(evt.getType()==CheckThreadUpdateEvent.Type.PROGRESS) {
                        ConsoleViewManager.printToConsole(project, evt.getMessage());
                    }
                }
            });
            
            runCheckThread(inputBean, srcDirList,project);
        }
    }

    /**
     * Clear all previous view output
     */
    public static void clear() {
        ErrorViewManager.clear();
    }

    // Helper function, this runs on the EDT thread
    //@ThreadConfined(ThreadName.EDT)
    private static void runCheckThread(final InputBean inputBean,
                                       final ArrayList<VirtualFile> srcDirList,
                                       final Project project)
    {
        Util.submit(new Runnable() {

            // worker thread
            //@ThreadConfined("worker")
            public void run() {
                ICheckThreadMain checkThread = CheckThreadMainFactory.newInstance(inputBean);
                try {

                    // Run checkthread and get timing information
                    long startTime = System.currentTimeMillis();
                    checkThread.run();
                    long stopTime = System.currentTimeMillis();
                    long runTime = stopTime - startTime;

                    ConsoleViewManager.printToConsole(project, "");
                    ConsoleViewManager.printToConsole(project, ResourceNames.FINISHED_MSG);
                    ConsoleViewManager.printToConsole(project, "Total analyze time: " + runTime + " ms.");

                } catch (CheckThreadException e) {
                    String message = e.getErrorMessageForClientName("Intellij");
                    ConsoleViewManager.printToConsole(project, message);
                    return;
                }

                reportCheckThreadErrors(checkThread,
                        srcDirList,
                        project);
            }
        });
    }

    // helper function, reports errors
    // runs on worker thread
    //@ThreadConfined("worker")
    private static void reportCheckThreadErrors(final ICheckThreadMain checkThread,
                                                final ArrayList<VirtualFile> srcDirList,
                                                final Project project) {
        SwingUtilities.invokeLater(new Runnable() {

            //runs on EDT thread
            //@ThreadConfined(ThreadName.EDT)
            public void run() {
                // PROCESS CHECKTHREAD ERRORS
                ArrayList<ICheckThreadError> errorList = checkThread.getErrors();

                // only create error view if necessary
                if(errorList.size()>0) {
                    ErrorViewManager.newErrorView(project);
                }

                // loop through and display errors
                for (ICheckThreadError error : errorList) {
                    VirtualFile vf = loadSrcFileFromProject(srcDirList,
                            error.getSourceFile());
                    if (vf != null) {
                        processError(vf,
                                project,
                                error);
                    }
                }

                if (errorList.size() < 1) {
                    if (!checkThread.foundClassFileToAnalyze()) {
                        ConsoleViewManager.printToConsole(project, ResourceNames.NO_CLASS_FILES_MSG);
                    } else if (!checkThread.foundCheckThreadAnnotations()) {
                        ConsoleViewManager.printToConsole(project, ResourceNames.NO_CHECKTHREAD_ANNOTATIONS_FOUND_MSG);
                    } else {
                        ConsoleViewManager.printToConsole(project, ResourceNames.NO_ERRORS_FOUND_MSG);
                    }
                } else {
                    ConsoleViewManager.printToConsole(project, "CheckThread found " + errorList.size() + " errors. See the 'CheckThread Error List' window for full error listing.");
                }
            }
        });
    }

    // Helper function for processing a single error point
    // @ThreadConfined(ThreadName.EDT)
    private static void processError(VirtualFile vf,
                                     Project project,
                                     ICheckThreadError error) {

        Document doc = FileDocumentManager.getInstance().getDocument(vf);

        // Add error stripe to editor
        if (doc != null) {
            System.out.println("Found document for: " + vf);
            MarkupModel mm = doc.getMarkupModel(project);
            // create error marker
            System.out.println("Found MarkupModel for " + vf);
            TextAttributes c = new TextAttributes(null,
                    null,
                    Color.red,
                    EffectType.WAVE_UNDERSCORE,
                    Font.PLAIN);
            //ToDo: Why offset -1?
            RangeHighlighter rh = mm.addLineHighlighter(error.getLineNumber() - 1,
                    HighlighterLayer.ERROR,
                    c);
            rh.setErrorStripeMarkColor(Color.red);
            rh.setErrorStripeTooltip(error.getErrorMessage());

            // Cache error marker so we can delete it later
            ErrorMarker errorMarker = new ErrorMarker();
            errorMarker.MarkupModel = mm;
            errorMarker.Project = project;
            errorMarker.RangeHighlighter = rh;
            errorMarker.VirtualFile = vf;
            ErrorViewManager.addErrorMarker(errorMarker);
        }

        ErrorTreeView errorView = ErrorViewManager.getErrorTreeView(project);
        if(errorView!=null) {
            errorView.addMessage(MessageCategory.ERROR,
                    new String[]{error.getErrorMessage()},
                    vf,
                    error.getLineNumber(),
                    -1,
                    null);
        }
    }

    // Get VirtualFile corresponding to the Java file
   // @ThreadSafe
    private static VirtualFile loadSrcFileFromProject(ArrayList<VirtualFile> srcDirList,
                                                      String javaFileName) {
        VirtualFile retval = null;
        for (VirtualFile srcDir : srcDirList) {
            try {
                File dir = VfsUtil.virtualToIoFile(srcDir);
                String absPathDir = dir.getAbsolutePath();
                // ToDo: Fragile code
                absPathDir = absPathDir.replace("\\", "/");
                javaFileName = javaFileName.replace("\\", "/");
                String srcPath = absPathDir + "/" + javaFileName;
                File srcFile = new File(srcPath);
                if (srcFile.isFile()) {
                    retval = VfsUtil.findFileByURL(srcFile.toURI().toURL());
                    System.out.println("Found file: " + retval);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retval;
    }

    // Convert VirtualFile to URI
    //@ThreadConfined(ThreadName.EDT)
    private static URI convertVirtualFiletoURI(VirtualFile vf) {
        URI retval = null;
        try {
            File file = VfsUtil.virtualToIoFile(vf);
            retval = file.toURI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    // Utility helper
    private static ArrayList<Module> getModulesForProject(final Project project) {
        final ArrayList<Module> moduleList = new ArrayList<Module>();

        if(project!=null && project.isOpen()) {
            ProjectRootManager pm = ProjectRootManager.getInstance(project);
            final ProjectFileIndex fi = pm.getFileIndex();
            fi.iterateContent(new ContentIterator() {
                public boolean processFile(VirtualFile fileOrDir) {
                    Module module = fi.getModuleForFile(fileOrDir);
                    if(!moduleList.contains(module)) {
                        moduleList.add(module);
                    }
                    return true;
                }
            });
        }
        return moduleList;
    }
}
