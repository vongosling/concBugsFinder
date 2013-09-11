package org.checkthread.main;

public class CheckThreadException extends Exception {
	
	private static final long serialVersionUID = 1;
	private Error fError;
	private Object fException;
	
	public CheckThreadException(Object e) {
		super();
		if(e instanceof Error) {
		    fError = (Error)e;
		} else if(e instanceof Exception ) {
			fException = (Exception)e;
		}
	}
	
	public String getErrorMessageForClientName(String clientName) {
		String errorMessage = null;
		if(fError instanceof UnsupportedClassVersionError) {
			errorMessage = "The CheckThread plugin requires that " + clientName + " run under a JRE that is the same or later version as the JDK for your Java project.\n"
			+ "Your options: \n"
			+ "1) Try compiling your Java code with an earlier JDK.\n"
			+ "2) Try running a later version of " + clientName + ".\n"
			+ "3) Make no changes and disable CheckThread.";
		} else if (fError instanceof NoClassDefFoundError) {
			errorMessage = "The CheckThread plug-in encountered a 'NoClassDefFound' Error.\n"
				+ "This may occur if you changed the JDK settings on your Java project.\n"
				+ "\n"
				+ "Your options: \n"
				+ "1) Try cleaning and rebuilding your Java project.\n"
				+ "2) If that doesn't fix the problem, disable CheckThread and report this bug to checkthread.org.";
		} else if(fException!=null) {
			errorMessage = fException.toString();
		} 
		return errorMessage;
	}
}
