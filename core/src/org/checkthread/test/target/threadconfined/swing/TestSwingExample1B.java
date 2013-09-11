package org.checkthread.test.target.threadconfined.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.checkthread.annotations.ThreadName;
import org.checkthread.annotations.ThreadConfined;

@ThreadConfined(ThreadName.EDT)
public class TestSwingExample1B extends JFrame {
	
	public TestSwingExample1B() {}
	
	@ThreadConfined(ThreadName.MAIN)
	public static void main(String argv[]) {
		SwingUtilities.invokeLater(new Runnable() {
			
			// ERROR Calling createFrame() on main thread
			//@ThreadPolicy(PolicyName.EDT)
			public void run() {
				JFrame f = new TestSwingExample1B();
				f.setSize(400, 180);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}