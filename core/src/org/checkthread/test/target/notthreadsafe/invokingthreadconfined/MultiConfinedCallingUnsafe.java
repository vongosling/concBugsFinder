package org.checkthread.test.target.notthreadsafe.invokingthreadconfined;

import org.checkthread.annotations.Scope;
import org.checkthread.annotations.ThreadConfined;
import org.checkthread.annotations.NotThreadSafe;

public class MultiConfinedCallingUnsafe {
	
private Inner fField = new Inner();

	@ThreadConfined("thread1")
	public void method2() {
		// ERROR, fField is shared across threads 
		fField.method1();
	}

	@ThreadConfined("thread2")
	public void method3() {
		// ERROR, fField is shared across threads
		fField.method1();
	}

	private static class Inner {

		@NotThreadSafe(synchronize = Scope.INSTANCE)
		public void method1() {
			System.out.println("Hello World");
		}
	}
}