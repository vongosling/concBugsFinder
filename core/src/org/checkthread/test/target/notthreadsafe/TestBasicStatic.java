package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.NotThreadSafe;
import org.checkthread.annotations.Scope;

public class TestBasicStatic {

	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public static void invalid() {
		//ERROR: Static method can't have sync scope less than CLASS 
		System.out.println("Hello World");
	}

	@NotThreadSafe(synchronize=Scope.CLASS)
	public static void threadUnsafeClass(){
		System.out.println("Hello World");
	}

	@NotThreadSafe
	public static void threadUnsafeLib() {
		System.out.println("Hello World");
	}
}
