package examples.swing.example2;

// Example comes from "Java Swing, 2nd Edition"
//http://examples.oreilly.com/jswing2/code/

//ActionExample.java
//An application that shows the Action class in, well, action.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.EDT)
public class ActionExample extends JFrame {

	public static final int MIN_CHANNEL = 2;
	public static final int MAX_CHANNEL = 13;

	private int currentChannel = MIN_CHANNEL;
	private int favoriteChannel = 9;
	private JLabel channelLabel = new JLabel();

	private Action upAction = new UpAction();
	private Action downAction = new DownAction();
	private GotoFavoriteAction gotoFavoriteAction = new GotoFavoriteAction();
	private Action setFavoriteAction = new SetFavoriteAction();

	@ThreadConfined(ThreadName.EDT)
	public class UpAction extends AbstractAction {
		public UpAction() {
			putValue(NAME, "Channel Up");
			putValue(SMALL_ICON, new ImageIcon("images/up.gif"));
			putValue(SHORT_DESCRIPTION, "Increment the channel number");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
		}

		public void actionPerformed(ActionEvent ae) {
			setChannel(currentChannel + 1);
		}
	}

	@ThreadConfined(ThreadName.EDT)
	public class DownAction extends AbstractAction {
		public DownAction() {
			putValue(NAME, "Channel Down");
			putValue(SMALL_ICON, new ImageIcon("images/down.gif"));
			putValue(SHORT_DESCRIPTION, "Decrement the channel number");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
		}

		public void actionPerformed(ActionEvent ae) {
			setChannel(currentChannel - 1);
		}
	}

	@ThreadConfined(ThreadName.EDT)
	public class GotoFavoriteAction extends AbstractAction {
		public GotoFavoriteAction() {
			putValue(SMALL_ICON, new ImageIcon("images/fav.gif"));
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
			updateProperties();
		}

		public void updateProperties() {
			putValue(NAME, "Go to channel " + favoriteChannel);
			putValue(SHORT_DESCRIPTION, "Change the channel to "
					+ favoriteChannel);
		}

		public void actionPerformed(ActionEvent ae) {
			setChannel(favoriteChannel);
		}
	}

	@ThreadConfined(ThreadName.EDT)
	public class SetFavoriteAction extends AbstractAction {
		public SetFavoriteAction() {
			putValue(NAME, "Set 'Go to' channel");
			putValue(SMALL_ICON, new ImageIcon("images/set.gif"));
			putValue(SHORT_DESCRIPTION,
					"Make current channel the Favorite channel");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
		}

		@ThreadConfined(ThreadName.EDT)
		public void actionPerformed(ActionEvent ae) {
			favoriteChannel = currentChannel;
			gotoFavoriteAction.updateProperties();
			setEnabled(false);
			gotoFavoriteAction.setEnabled(false);
		}
	}

	public void foo1() {
		toString().toString();
	}
	
	public void foo2() {
		toString();
		toString();
	}
	
	public void foo() {
		getContentPane().add(channelLabel, BorderLayout.NORTH);		
	}
	
	@ThreadConfined(ThreadName.EDT)
	public ActionExample() {
		super("ActionExample");

    	setChannel(currentChannel); // enable/disable the Actions as appropriate

		channelLabel.setHorizontalAlignment(JLabel.CENTER);

		channelLabel.setFont(new Font("Serif", Font.PLAIN, 32));

		getContentPane().add(channelLabel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 16, 6));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 16, 16, 16));
		getContentPane().add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.add(new JButton(upAction));
		buttonPanel.add(new JButton(gotoFavoriteAction));
		buttonPanel.add(new JButton(downAction));
		buttonPanel.add(new JButton(setFavoriteAction));

		JMenuBar mb = new JMenuBar();
		JMenu menu = new JMenu("Channel");
		menu.add(new JMenuItem(upAction));
		menu.add(new JMenuItem(downAction));
		menu.addSeparator();
		menu.add(new JMenuItem(gotoFavoriteAction));
		menu.add(new JMenuItem(setFavoriteAction));
		mb.add(menu);
		setJMenuBar(mb);
	}

	@ThreadConfined(ThreadName.EDT)
	public void setChannel(int chan) {
		currentChannel = chan;
		channelLabel.setText("Now tuned to channel: " + currentChannel);
		// enable/disable the Actions as appropriate
		downAction.setEnabled(currentChannel > MIN_CHANNEL);
		upAction.setEnabled(currentChannel < MAX_CHANNEL);
		gotoFavoriteAction.setEnabled(currentChannel != favoriteChannel);
		setFavoriteAction.setEnabled(currentChannel != favoriteChannel);
	}

	@ThreadConfined(ThreadName.MAIN)
	public static void main(String argv[]) {
		SwingUtilities.invokeLater(new Runnable() {

			@ThreadConfined(ThreadName.EDT)
			public void run() {
				JFrame f = new ActionExample();
				f.setSize(400, 180);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);
			}
		});
	}
}
