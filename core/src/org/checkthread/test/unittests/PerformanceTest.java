package org.checkthread.test.unittests;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

import org.checkthread.main.CheckThreadMainFactory;
import org.checkthread.main.ICheckThreadError;
import org.checkthread.main.ICheckThreadMain;
import org.checkthread.main.InputBean;
import org.checkthread.util.FileUtil;

/**
 * For interactive stress testing
 * This is not used with jUNIT. 
 */
public class PerformanceTest {

	public static void main(String[] args) throws Exception {
	   startBench();	
	}
	
	public static void startBench() throws Exception {
        runbench();
	}

	public static void runbench() throws Exception {
	
		long startTime = System.currentTimeMillis();
		run(null,null);
		long stopTime = System.currentTimeMillis();
		long runTime = stopTime - startTime;
		System.out.println("Bench mark time: " + runTime);
	}
	public static void run(ArrayList<URI> classPath,
			               ArrayList<URI> targetPath) throws Exception {
		if(classPath==null) {
			classPath = new ArrayList<URI>();
			File rootDir = new File("C:/TEMP/ct/jar");
			recurseGetJars(classPath, rootDir);
		} 
		
		if(targetPath==null) {
			targetPath = new ArrayList<URI>();
			File file = new File("C:/TEMP/ct/src/out/");
			targetPath.add(file.toURI());	
		}
		InputBean inputBean = InputBean.newInstance();
		inputBean.setVerboseLevel(0);
		inputBean.setTargetPath(targetPath);
		inputBean.setClassPath(classPath);
		inputBean.setRecurse(true);
		ICheckThreadMain checkThread = CheckThreadMainFactory
				.newInstance(inputBean);
		checkThread.run();
		ArrayList<ICheckThreadError> errorList = checkThread.getErrors();
		System.out.println("Error count: " + errorList.size());
		System.out.println("Found Annotations: " + checkThread.foundCheckThreadAnnotations());
	}

	private static void recurseGetJars(ArrayList<URI> classPath, File rootDir) {
		File[] fileList = rootDir.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				recurseGetJars(classPath, file);
			} else if (file.isFile() && file.getName().endsWith(".jar")) {
				classPath.add(file.toURI());
			}
		}
	}
}
