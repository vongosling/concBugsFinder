package org.checkthread.test.target.threadconfined.custom;

import org.checkthread.annotations.*;
import java.lang.reflect.*;

@ThreadConfined("thread1")
public class TestStaticBlock2 {

	private static class Foo extends Object {
		
		// runs on main thread
		static {
			foo(); // ERROR: calling foo on main thread
		}
	}
	
	@ThreadConfined("thread2")
	private static void foo() {}
}

