package org.checkthread.test.target.threadconfined.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.checkthread.annotations.ThreadName;
import org.checkthread.annotations.ThreadConfined;

@ThreadConfined(ThreadName.EDT)
public class TestSwingExample3 {

	public TestSwingExample3() {}

	@ThreadConfined(ThreadName.MAIN)
	public static void main(String s[]) {
		SwingUtilities.invokeLater(new Runnable() {

		
			// algorithm notes:
			// obtain thread policy for a method
			// look at method
			// look at class
			// look at 
			//@ThreadPolicy(PolicyName.EDT)
			public void run() {
				//init();
				foo();
				new Runnable() {
					public void run() {
					//	init();
					}
				};
			}
			
			private void foo() {
				JFrame frame = new JFrame("Simple List Example");
			}
		});
	}

	private static void methodEDT() {
		JFrame frame = new JFrame("Simple List Example");
	}
}
