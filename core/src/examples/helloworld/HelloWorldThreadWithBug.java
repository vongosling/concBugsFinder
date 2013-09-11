package examples.helloworld;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class HelloWorldThreadWithBug extends Thread {

    public HelloWorldThreadWithBug() {
		// runs main thread 
		super();
		start();
		helloworld(); // THREAD BUG HERE!!!
	}

    @ThreadConfined("helloWorldThread")
	public void run() {
    	// runs hello world thread  
	    helloworld();
	}

    @ThreadConfined("helloWorldThread")
    public void helloworld() {
    	// should run on hello world thread   
    	System.out.println("Hello World!");
    }
    
	public static void main(String[] args) {
		// runs main thread 
		new HelloWorldThreadWithBug();
	}
}