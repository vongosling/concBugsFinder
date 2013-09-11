package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.*;

public class TestThreadUnSafe1 {

	@NotThreadSafe(synchronize=Scope.CLASS)
	public void method() {
		TestBasicInstance lib = new TestBasicInstance();
		lib.threadUnsafeInstance(); // OK, method1 has a smaller sync requirement	
	}
}
