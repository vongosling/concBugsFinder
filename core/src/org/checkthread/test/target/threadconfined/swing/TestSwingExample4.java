package org.checkthread.test.target.threadconfined.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.checkthread.annotations.ThreadName;
import org.checkthread.annotations.ThreadConfined;

@ThreadConfined(ThreadName.MAIN)
public class TestSwingExample4 {

	public TestSwingExample4() {}

	public static void main(String s[]) {
		SwingUtilities.invokeLater(new MyRunnable());
	}

	private static class MyRunnable implements Runnable {

		//ERROR: Calling EDT from MAIN
		public void run() {
			bar();
		}	
	}
	
	@ThreadConfined(ThreadName.EDT)
	private static void bar() {
		JFrame frame = new JFrame("Simple List Example");
	}
}
