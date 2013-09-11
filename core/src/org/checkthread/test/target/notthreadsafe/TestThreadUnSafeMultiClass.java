package org.checkthread.test.target.notthreadsafe;

import org.checkthread.annotations.*;

public class TestThreadUnSafeMultiClass {

	class CallingClass1 {
		@NotThreadSafe(synchronize=Scope.INSTANCE)
		public void method1() {
			TestBasicInstance lib = new TestBasicInstance();
			lib.threadUnsafeInstance(); // OK, method1 has a smaller sync requirement	
		}
	}

	class CallingClass2 {
		@NotThreadSafe(synchronize=Scope.INSTANCE)
		public void method1() {
			TestBasicInstance lib = new TestBasicInstance();
			lib.threadUnsafeInstance(); // OK, method1 has a smaller sync requirement	
		}
	}

	class CallingClass3 {

		@NotThreadSafe(synchronize=Scope.INSTANCE)
		public void method1() {
			CallingClass1 c1 = new CallingClass1();
			c1.method1(); // OK, no shared data

			CallingClass2 c2 = new CallingClass2();
			c2.method1(); //OK, no shared data
		}
	}
}