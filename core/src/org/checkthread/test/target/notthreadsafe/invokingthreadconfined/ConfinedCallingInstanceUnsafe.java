package org.checkthread.test.target.notthreadsafe.invokingthreadconfined;

import org.checkthread.annotations.*;

public class ConfinedCallingInstanceUnsafe {

	Inner fField;

	@ThreadConfined("mythread")
	public void methodThreadConfined1() {
		//OK, no shared data, no need to synchronize on instance
		Inner n = new Inner();
		n.methodThreadUnsafe();
	}	

	@ThreadConfined("mythread")
	public void methodThreadConfined2() {
		//OK, no shared data, no need to synchronize on instance
		Inner n = new Inner();
		n.methodThreadUnsafe();
	}
	
	@ThreadConfined("mythread")
	public void methodThreadConfined3() {
		//ERROR, shared data, must synchronize on instance
		fField.methodThreadUnsafe();
	}

	@ThreadConfined("mythread")
	public void methodThreadConfined4() {
		//ERROR, must synchronize on class
		Inner.methodThreadUnsafe2();
	}
	
	public static class Inner {
		@NotThreadSafe(synchronize=Scope.INSTANCE)
		public void methodThreadUnsafe() {
			System.out.println("Hello World");
		}		
		
		@NotThreadSafe(synchronize=Scope.CLASS)
		public static void methodThreadUnsafe2() {
			System.out.println("hello world");
		}
	}
}
