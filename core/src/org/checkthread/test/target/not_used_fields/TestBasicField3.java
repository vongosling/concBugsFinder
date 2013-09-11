package org.checkthread.test.target.not_used_fields;

public class TestBasicField3 {

	private MyClass foo = new MyClass();
	private MyClass bar = new MyClass(2);
	private MyClass foofoo = MyClass.newInstance();
	
	private static class MyClass {
		public MyClass() {}
		public MyClass(int f) {}
		public static MyClass newInstance() {
			return new MyClass();
		}
	}
}

