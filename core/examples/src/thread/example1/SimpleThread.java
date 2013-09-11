package examples.thread.example1;

//: c13:SimpleThread.java
//Very simple Threading example.
//From 'Thinking in Java, 3rd ed.' (c) Bruce Eckel 2002
//www.BruceEckel.com. See copyright notice in CopyRight.txt.

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class SimpleThread extends Thread {
	
	private int countDown;
	
	private static int threadCount = 0;

	public SimpleThread() {
		super("" + ++threadCount); // Store the thread name
		countDown = 5;
		start();
	}

	@ThreadConfined("mythread")
	public String toString() {
		return "#" + getName() + ": " + countDown;
	}

	@ThreadConfined("mythread")
	public void run() {
		while (true) {
			System.out.println(this.toString());
			if (--countDown == 0)
				return;
		}
	}

	
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++)
			new SimpleThread();
	}
} ///:~

