package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.NotThreadSafe;
import org.checkthread.annotations.Scope;

public class TestThreadUnSafe2 {
	
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method1BBB() {
		//ERROR: Instance method must synchronize
		//when calling methods with CLASS scope.
		TestBasicStatic.threadUnsafeClass();  	
	}

	@NotThreadSafe(synchronize=Scope.CLASS)
	public static void method2() {
		TestBasicStatic.threadUnsafeClass(); //OK  	
	}
		
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void method3() {
		synchronized(this.getClass()) {
			TestBasicStatic.threadUnsafeClass(); // OK
		}
	}
}
