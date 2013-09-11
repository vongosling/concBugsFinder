package examples.swing.example2;

//Example comes from "Java Swing, 2nd Edition"
//http://examples.oreilly.com/jswing2/code/

//ClockTest.java
//A demonstration framework for the Timer driven ClockLabel class

import javax.swing.*;
import java.awt.*;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.EDT)
public class ClockTest extends JFrame {

	@ThreadConfined(ThreadName.EDT)
	public ClockTest() {
		super("Timer Demo");
		setSize(300, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		ClockLabel clock = new ClockLabel();
		getContentPane().add(clock, BorderLayout.NORTH);
	}

	@ThreadConfined(ThreadName.MAIN)
	public static void main(String args[]) {
     SwingUtilities.invokeLater(new Runnable()
     {
     	@ThreadConfined(ThreadName.EDT)
     	public void run() {
     		ClockTest ct = new ClockTest();
     		ct.setVisible(true);
     	}
     });
	}
}

