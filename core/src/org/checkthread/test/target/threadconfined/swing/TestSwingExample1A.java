package org.checkthread.test.target.threadconfined.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.checkthread.annotations.ThreadName;
import org.checkthread.annotations.ThreadConfined;

@ThreadConfined(ThreadName.EDT)
public class TestSwingExample1A extends JFrame {
	
	public static JFrame createFrame() {
		return null;
	}
	
	@ThreadConfined(ThreadName.MAIN)
	public static void main(String argv[]) {
		SwingUtilities.invokeLater(new Runnable() {

			// ERROR Calling createFrame() on main thread
			public void run() {
				JFrame f = createFrame();
				f.setSize(400, 180);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}
