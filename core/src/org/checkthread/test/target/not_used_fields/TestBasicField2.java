package org.checkthread.test.target.not_used_fields;

import org.checkthread.annotations.*;

public class TestBasicField2 implements Runnable {

	private static class MyInnerClass {	
		public MyInnerClass() {
			System.out.println("MyInnerClass: " + Thread.currentThread());
		}
		
		public void finalize() {
			System.out.println("finalize: " + Thread.currentThread());
		}
	}

	private static class MyInnerClass2 {
		private static MyInnerClass foo = new MyInnerClass();
		private MyInnerClass foo2 = new MyInnerClass();

		public MyInnerClass2() {
			MyInnerClass f= new MyInnerClass();
			f = null;
			System.gc();
		}
	}

	@ThreadConfined("testThread")
	public void run() {
		System.out.println("run: " + Thread.currentThread());
		new MyInnerClass2();
	}
	
	@ThreadConfined(ThreadName.MAIN)
	public static void main(String[] args) {
		MyInnerClass f= new MyInnerClass();
		f = null;
		System.gc();
		Thread t = new Thread(new TestBasicField2());
		t.start();
	}
}
