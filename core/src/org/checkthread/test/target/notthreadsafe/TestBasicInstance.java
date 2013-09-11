package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.NotThreadSafe;
import org.checkthread.annotations.Scope;

public class TestBasicInstance {
	
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void threadUnsafeInstance() {
	   System.out.println("Hello World");
	}
	
	@NotThreadSafe(synchronize=Scope.CLASS)
	public void threadUnsafeClass() {
	   System.out.println("Hello World");
	}
	
	@NotThreadSafe
	public void threadUnsafeLibrary() {
	   System.out.println("Hello World");
	}
}
