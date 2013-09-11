package examples.helloworld;

import org.checkthread.annotations.ThreadName;
import org.checkthread.annotations.ThreadConfined;

@ThreadConfined(ThreadName.MAIN)
public class HelloWorldThreadClassAnnotation extends Thread {

    public HelloWorldThreadClassAnnotation() {
		// runs main thread 
		super();
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
    
	public static void main(String[] args) {
		// runs main thread 
		new HelloWorldThreadClassAnnotation();
	}
}