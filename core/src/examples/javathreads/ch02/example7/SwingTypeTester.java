package examples.javathreads.ch02.example7;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.checkthread.annotations.*;

import examples.javathreads.ch02.CharacterDisplayCanvas;
import examples.javathreads.ch02.CharacterEventHandler;
import examples.javathreads.ch02.CharacterListener;
import examples.javathreads.ch02.CharacterSource;

@ThreadConfined(ThreadName.EDT)
public class SwingTypeTester extends JFrame implements CharacterSource {
	
    protected RandomCharacterGenerator producer;
    private AnimatedCharacterDisplayCanvas displayCanvas;
    private CharacterDisplayCanvas feedbackCanvas;
    private JButton quitButton;
    private JButton startButton;
    private JButton stopButton;
    private CharacterEventHandler handler;

    @ThreadConfined(ThreadName.EDT)
    public SwingTypeTester() {
        initComponents();
    }
    
    @ThreadConfined(ThreadName.EDT)
    private void initComponents() {
        handler = new CharacterEventHandler();
        displayCanvas = new AnimatedCharacterDisplayCanvas();
        feedbackCanvas = new CharacterDisplayCanvas(this);
        quitButton = new JButton();
        startButton = new JButton();
        stopButton = new JButton();
        add(displayCanvas, BorderLayout.NORTH);
        add(feedbackCanvas, BorderLayout.CENTER);
        JPanel p = new JPanel();
        startButton.setLabel("Start");
        stopButton.setLabel("Stop");
        stopButton.setEnabled(false);
        quitButton.setLabel("Quit");
        p.add(startButton);
        p.add(stopButton);
        p.add(quitButton);
        add(p, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
            	System.out.println("windowClosing thread: " + Thread.currentThread());
                quit();
            }
        });

        feedbackCanvas.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (c != KeyEvent.CHAR_UNDEFINED)
                    newCharacter((int) c);
            }
        });
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                producer = new RandomCharacterGenerator();
                displayCanvas.setCharacterSource(producer);
                producer.start();
                displayCanvas.setDone(false);
                Thread t = new Thread(displayCanvas);
                t.setName(displayCanvas.THREAD_NAME);
                t.start();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                feedbackCanvas.setEnabled(true);
                feedbackCanvas.requestFocus();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                producer.setDone();
                displayCanvas.setDone(true);
                feedbackCanvas.setEnabled(false);
            }
        });
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quit();
            }
        });
            

        pack();
    }
   
    @ThreadConfined(ThreadName.EDT)
    private void quit() {
    	System.out.println("SwingTypeTester.quit: " + Thread.currentThread());
        System.exit(0);
    }

    @ThreadConfined(ThreadName.EDT)
    public void addCharacterListener(CharacterListener cl) {
    	System.out.println("SwingTypeTester.addCharacterListener: " + Thread.currentThread());
        handler.addCharacterListener(cl);
    }
    
    @ThreadConfined(ThreadName.EDT)
    public void removeCharacterListener(CharacterListener cl) {
    	System.out.println("SwingTypeTester.removeCharacterListener: " + Thread.currentThread());
        handler.removeCharacterListener(cl);
    }

    //@ThreadPolicy(AnimatedCharacterDisplayCanvas.THREAD_NAME)
    @ThreadConfined(ThreadName.EDT)
    public void newCharacter(int c) {
    	System.out.println("SwingTypeTester.newCharacter: " + Thread.currentThread());
        handler.fireNewCharacter(this, c);
    }

    @ThreadConfined(AnimatedCharacterDisplayCanvas.THREAD_NAME)
    public void nextCharacter() {
    	System.out.println("SwingTypeTester.nextCharacter: " + Thread.currentThread());
        throw new IllegalStateException("We don't produce on demand");
    }
    
    private static class MyRunnable implements Runnable {	

    	   @ThreadConfined(ThreadName.EDT)
    		public void run() {
    	        new SwingTypeTester().show();	
    		}
    }

    @ThreadConfined(ThreadName.MAIN)
    public static void main(String args[]) {
    	// BUG: This should be called on the event thread
    	
    	SwingUtilities.invokeLater(new MyRunnable());
    }
}
