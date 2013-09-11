package org.checkthread.test.target.inherit_annotations;

import org.checkthread.annotations.*;

public class TestNoAnnotation {
    
	public void methodNoAnnotation1() {}
	public void methodNoAnnotation2() {}
	public void methodNoAnnotation3() {}
	public void methodNoAnnotation4() {}
	
	public class InnerClass extends TestNoAnnotation {
	    
		// OK
		public void methodNoAnnotation1() {}
	   
		@ThreadSafe // OK
		public void  methodNoAnnotation2() {}
		
		@NotThreadSafe // OK
		public void  methodNoAnnotation3() {}
		
		@ThreadConfined("foo") // OK
		public void  methodNoAnnotation4() {}
	}
}
