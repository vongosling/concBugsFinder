package examples.helloworld;

import org.checkthread.annotations.*;

public class HelloWorldThread extends Thread {

	@ThreadConfined(ThreadName.MAIN)
    public HelloWorldThread() {
		// runs main thread 
		super();
		setName("helloWorldThread");
		start();
	}

    @ThreadConfined("helloWorldThread")
	public void run() {
    	// runs hello world thread  
	    helloworld();
	}

    @ThreadConfined("helloWorldThread")
    public void helloworld() {
    	// runs hello world thread 
    	System.out.println("Hello World!");
    }
    
	@ThreadConfined(ThreadName.MAIN)
	public static void main(String[] args) {
		// runs main thread 
		new HelloWorldThread();
	}
}
