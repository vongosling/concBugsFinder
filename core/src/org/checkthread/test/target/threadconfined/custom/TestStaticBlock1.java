package org.checkthread.test.target.threadconfined.custom;

import org.checkthread.annotations.*;
import java.lang.reflect.*;

@ThreadConfined("thread1")
public class TestStaticBlock1 {

	// runs on main thread
	static {
		//ERROR: foo is called on main thread
		foo();
	}
	
	@ThreadConfined("thread2")
	public static void foo() {}
}
