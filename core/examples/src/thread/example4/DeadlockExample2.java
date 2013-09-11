package examples.thread.example4;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class DeadlockExample2 {
	
	@ThreadSafe
	public static void foo(Object resource1, Object resource2) {
		
		// lock resource1
		synchronized (resource1) {
			System.out.println("Thread 1: locked resource 1");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			
			// lock resource2
			synchronized (resource2) {
				System.out.println("Thread 1: locked resource 2");
			}
		}
	}
	
	public static void main(String[] args) {
		final Object resource1 = "resource1";
		final Object resource2 = "resource3";

		Thread t1 = new Thread() {
			
			@ThreadConfined("t1")
			public void run() {
			    foo(resource1,resource2);
			}
		};

        Thread t2 = new Thread() {
        	
        	@ThreadConfined("t2")
			public void run() {
               foo(resource2,resource1);
			}
		};

		t1.start();
		t2.start();
	}
}
