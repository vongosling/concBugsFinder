package org.checkthread.test.target.not_used_fields;

import org.checkthread.annotations.*;

public class TestBasicField {

	// runs on "outer" thread
	private InnerClass foo = new InnerClass();
	
	@ThreadConfined("thread1")
	private static class InnerClass {
		public InnerClass() {
			
		}
	}
	
	@ThreadConfined(ThreadName.MAIN)
	public TestBasicField() {
		
	}
	
	@ThreadConfined(ThreadName.MAIN)
	public static void main(String [] args) {
		new TestBasicField();
	}
}
