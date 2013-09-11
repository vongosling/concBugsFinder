package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.*;

public class TestSharedInstance {
	public static TestBasicInstance sField;
}

class CallingClassINSTANCE1 {
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method1() {
		//ERROR: Method must synchronize calls to method1()
		//since sLibrary is a public field and can be shared
		//among many classes.
		TestSharedInstance.sField.threadUnsafeInstance();
		
		// Error (see above)
		TestSharedInstance.sField.threadUnsafeClass();
		
		// Error (see above)
		TestSharedInstance.sField.threadUnsafeLibrary();
	}
}

class CallingClassINSTANCE2 {
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method1() {
		//ERROR: Method must synchronize calls to method
		//since sLibrary is a public field and can be shared 
		//among many classes.
		TestSharedInstance.sField.threadUnsafeInstance();
	}
}

class CallingClassINSTANCE3 {
	@ThreadSafe
	public synchronized void method1() {
	   CallingClassINSTANCE1 c = new CallingClassINSTANCE1();
	   c.method1(); // OK
	}	
	
	@ThreadSafe
	public synchronized void method2() {
	   CallingClassINSTANCE2 c = new CallingClassINSTANCE2();
	   c.method1(); // OK
	}
}
