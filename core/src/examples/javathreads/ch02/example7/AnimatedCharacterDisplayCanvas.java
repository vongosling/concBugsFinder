package examples.javathreads.ch02.example7;

import java.awt.*;

import org.checkthread.annotations.*;

import examples.javathreads.ch02.CharacterDisplayCanvas;
import examples.javathreads.ch02.CharacterEvent;
import examples.javathreads.ch02.CharacterListener;
import examples.javathreads.ch02.CharacterSource;

@ThreadConfined(ThreadName.MAIN)
public class AnimatedCharacterDisplayCanvas extends CharacterDisplayCanvas implements CharacterListener, Runnable {

	final public static String THREAD_NAME = "javathreads.examples.ch02.example7.AnimatedCharacterDisplayCanvas";

    private volatile boolean done = false;
    private int curX = 0;

    @ThreadConfined(ThreadName.EDT)
    public AnimatedCharacterDisplayCanvas() {
    	System.out.println("Animated.const2: " + Thread.currentThread());
    }
    
    @ThreadConfined(ThreadName.EDT)
    public AnimatedCharacterDisplayCanvas(CharacterSource cs) {
        super(cs);
        System.out.println("Animated.const2: " + Thread.currentThread());
    }

    @ThreadConfined(RandomCharacterGenerator.THREAD_NAME)
    public synchronized void newCharacter(CharacterEvent ce) {
    	System.out.println("Animated.newCharacter: " + Thread.currentThread());
        curX = 0;
        tmpChar[0] = (char) ce.character;
        repaint();
    }

    @ThreadConfined(ThreadName.EDT)
    protected synchronized void paintComponent(Graphics gc) {
    	System.out.println("Animated.paintComponent: " + Thread.currentThread());
        Dimension d = getSize();
        gc.clearRect(0, 0, d.width, d.height);
        if (tmpChar[0] == 0)
            return;
        int charWidth = fm.charWidth(tmpChar[0]);
        gc.drawChars(tmpChar, 0, 1,
                     curX++, fontHeight);
    }

    @ThreadConfined(AnimatedCharacterDisplayCanvas.THREAD_NAME)
    public void run() {
    	System.out.println("Animated.run: " + Thread.currentThread());
        while (!done) {
            repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                return;
            }
        }
    }

    @ThreadConfined(ThreadName.EDT)
    public void setDone(boolean b) {
    	System.out.println("Animated.setDone: " + Thread.currentThread());
        done = b;
    }
}
