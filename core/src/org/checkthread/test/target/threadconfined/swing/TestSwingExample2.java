package org.checkthread.test.target.threadconfined.swing;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class TestSwingExample2 {

	public TestSwingExample2() {}

	public static void main(String s[]) {
		SwingUtilities.invokeLater(new MyRunnable());
	}

	private static class MyRunnable implements Runnable {

		public void run() {
			bar(); // ERROR, Need to specify EDT annotation
		}	
	}
	
	@ThreadConfined(ThreadName.EDT)
	private static void bar() {
		JFrame frame = new JFrame("Simple List Example");
	}
	
	private void foo() {
		System.out.println("Hello World");
	}
}
