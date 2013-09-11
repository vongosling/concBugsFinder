package org.checkthread.test.target.inherit_annotations;

import org.checkthread.annotations.*;

public class TestThreadUnsafe  {
	
	@NotThreadSafe
	public void methodThreadUnsafe1() {}
	
    @NotThreadSafe
	public void methodThreadUnsafe2() {}
	
	@NotThreadSafe(synchronize=Scope.CLASS)
	public void methodThreadUnsafe3A() {}

	@NotThreadSafe(synchronize=Scope.CLASS)
	public void methodThreadUnsafe3B() {}
    
	@NotThreadSafe
	public void methodThreadUnsafe4() {}
	
	public class InnerClass extends TestThreadUnsafe {
		
		// ERROR, should be @ThreadUnsafe
		public void methodThreadUnsafe1() {}
		
		// ERROR
		@ThreadSafe
		public void methodThreadUnsafe2() {}
		
		// ERROR
		@NotThreadSafe
		public void methodThreadUnsafe3A() {}

		// OK
		@NotThreadSafe(synchronize=Scope.CLASS)
		public void methodThreadUnsafe3B() {}
		
		// ERROR, should be @ThreadUnsafe
		@ThreadConfined("foo")
		public void methodThreadUnsafe4() {}	
	}
}