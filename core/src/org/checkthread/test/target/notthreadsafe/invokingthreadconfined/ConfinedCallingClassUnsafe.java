package org.checkthread.test.target.notthreadsafe.invokingthreadconfined;

import org.checkthread.annotations.*;

public class ConfinedCallingClassUnsafe {
		
	@ThreadConfined("mythread")
	public void methodThreadConfined() {
		// ERROR, synchronization on class required
		Inner.methodThreadUnsafe();
	}	
	
	public static class Inner {
		@NotThreadSafe(synchronize=Scope.CLASS)
		public static void methodThreadUnsafe() {
			System.out.println("Hello World");
		}		
	}
}