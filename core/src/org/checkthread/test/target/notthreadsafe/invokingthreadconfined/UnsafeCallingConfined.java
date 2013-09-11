package org.checkthread.test.target.notthreadsafe.invokingthreadconfined;

import org.checkthread.annotations.*;

public class UnsafeCallingConfined {
	
	@NotThreadSafe(synchronize=Scope.INSTANCE)
	public void methodThreadUnsafe1() {
		methodThreadConfined(); // ERROR, calling thread confined
	}
	
	@NotThreadSafe(synchronize=Scope.CLASS)
	public void methodThreadUnsafe2() {
		methodThreadConfined(); // ERROR, calling thread confined
	}

	@NotThreadSafe
	public void methodThreadUnsafe3() {
		methodThreadConfined(); // ERROR, calling thread confined
	}
	
	@ThreadConfined(ThreadName.MAIN)
	public void methodThreadConfined() {
		System.out.println("Hello World");
	}
}
