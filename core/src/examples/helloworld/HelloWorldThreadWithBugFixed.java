package examples.helloworld;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class HelloWorldThreadWithBugFixed extends Thread {

    public HelloWorldThreadWithBugFixed() {
		// runs main thread
		super();
		start();
		helloworld(); // OKAY
	}

    @ThreadConfined("helloWorldThread")
	public void run() {
    	// runs hello world thread
	    helloworld();
	}

    @ThreadSafe
    public void helloworld() {
    	// this method can run on any thread
    	System.out.println("Hello World!");
    }

	public static void main(String[] args) {
		// runs main thread
		new HelloWorldThreadWithBugFixed();
	}
}