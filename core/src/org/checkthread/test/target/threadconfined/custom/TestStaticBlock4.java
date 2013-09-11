package org.checkthread.test.target.threadconfined.custom;

import org.checkthread.annotations.*;
import java.lang.reflect.*;

@ThreadConfined("thread1")
public class TestStaticBlock4 {

	@ThreadConfined(ThreadName.MAIN)
	private static class Foo extends Object {
		
		// runs on main thread
		static {
			Runnable r = new Runnable() {
				
	            @ThreadConfined("thread2")
				public void run() {
					foo(); // OK: calling foo on thread2					
				}
			};
		}
	}
	
	@ThreadConfined("thread2")
	private static void foo() {}
}

