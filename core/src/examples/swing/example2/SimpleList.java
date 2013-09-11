package examples.swing.example2;

//Example comes from "Java Swing, 2nd Edition"
//http://examples.oreilly.com/jswing2/code/

//SimpleList.java
//A simple example of a JList object built from an array of Strings.
//

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.EDT)
public class SimpleList extends JPanel {
	String label[] = { "Zero", "One", "Two", "Three", "Four", "Five", "Six",
			"Seven", "Eight", "Nine", "Ten", "Eleven" };
	JList list;

	@ThreadConfined(ThreadName.EDT)
	public SimpleList() {
		this.setLayout(new BorderLayout());
		list = new JList(label);
		JScrollPane pane = new JScrollPane(list);
		JButton button = new JButton("Print");
		button.addActionListener(new PrintListener());

		add(pane, BorderLayout.CENTER);
		add(button, BorderLayout.SOUTH);
	}

	@ThreadConfined(ThreadName.MAIN)
	public static void main(String s[]) {
     SwingUtilities.invokeLater(new Runnable() {
     	
     	@ThreadConfined(ThreadName.EDT)
     	public void run() {
     		initfoo();
     	}
     });
	}

	@ThreadConfined(ThreadName.EDT)
	private static void initfoo() {
		JFrame frame = new JFrame("Simple List Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new SimpleList());
		frame.setSize(250, 200);
		frame.setVisible(true);	
	}
	
	// An inner class to respond to clicks on the Print button
	@ThreadConfined(ThreadName.EDT)
	class PrintListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int selected[] = list.getSelectedIndices();
			System.out.println("Selected Elements:  ");

			for (int i = 0; i < selected.length; i++) {
				String element = (String) list.getModel().getElementAt(
						selected[i]);
				System.out.println("  " + element);
			}
		}
	}
}
