package org.checkthread.test.target.inherit_annotations;

import javax.swing.JButton;
import javax.swing.border.Border;

import org.checkthread.annotations.ThreadConfined;
import org.checkthread.annotations.ThreadSafe;
import org.checkthread.annotations.NotThreadSafe;

public class TestThreadConfined extends JButton  {
	
	// OK
	// Implicit EDT should not throw error here
	public void setBorder(Border border) {}
	
	@ThreadConfined("foo")
	public void methodThreadConfined1() {}

	@ThreadConfined("foo")
	public void methodThreadConfined2() {}
	
	@ThreadConfined("foo")
	public void methodThreadConfined3() {}
	
	@ThreadConfined("foo")
	public void methodThreadConfined4() {}
	
	public class InnerClass extends TestThreadConfined {
		
		// ERROR
		@ThreadSafe
		public void methodThreadConfined1() {}

		// ERROR
		@NotThreadSafe
		public void methodThreadConfined2() {}
		
		// OK
		@ThreadConfined("foo")
		public void methodThreadConfined3() {}

		// ERROR
		@ThreadConfined("bar")
		public void methodThreadConfined4() {}		
	}
}