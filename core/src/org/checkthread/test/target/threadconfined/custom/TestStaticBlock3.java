package org.checkthread.test.target.threadconfined.custom;

import org.checkthread.annotations.*;
import java.lang.reflect.*;

@ThreadConfined("thread2")
public class TestStaticBlock3 {

	private static class Foo extends Object {
		
		// runs on main thread
		static {
			foo(); // OK: calling foo on main thread
		}
	}
	
	@ThreadConfined(ThreadName.MAIN)
	private static void foo() {}
}
