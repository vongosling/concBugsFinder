package org.checkthread.test.target.inherit_annotations;

import org.checkthread.annotations.ThreadConfined;
import org.checkthread.annotations.ThreadSafe;
import org.checkthread.annotations.NotThreadSafe;

public class TestThreadSafe  {
	
	@ThreadSafe
	public void methodThreadSafe1() {}
	
	@ThreadSafe
	public void methodThreadSafe2() {}

	@ThreadSafe
	public void methodThreadSafe3() {}
	
	@ThreadSafe
	public void methodThreadSafe4() {}
	
	public class InnerClass extends TestThreadSafe {
		
		// ERROR, should be @ThreadSafe
		public void methodThreadSafe1() {}
		
		// OK
		@ThreadSafe
		public void methodThreadSafe2() {}
		
		// ERROR, should be @ThreadSafe
		@NotThreadSafe
		public void methodThreadSafe3() {}
		
		// ERROR, should be @ThreadSafe
		@ThreadConfined("foo")
		public void methodThreadSafe4() {}	
	}
}
