package examples.javathreads.ch02.example7;

import java.util.*;

import org.checkthread.annotations.*;

import examples.javathreads.ch02.CharacterEventHandler;
import examples.javathreads.ch02.CharacterListener;
import examples.javathreads.ch02.CharacterSource;

@ThreadConfined(ThreadName.MAIN)
public class RandomCharacterGenerator extends Thread implements CharacterSource {
    static char[] chars;
    static String charArray = "abcdefghijklmnopqrstuvwxyz0123456789";
    static {
        chars = charArray.toCharArray();
    }

    Random random;
    CharacterEventHandler handler;

    private volatile boolean done = false;
    final public static String THREAD_NAME = "javathreads.examples.ch02.example7.RandomCharacterGenerator";
    
    @ThreadConfined(ThreadName.EDT)
    public RandomCharacterGenerator() {
    	System.out.println("Random.const1: " + Thread.currentThread());
        random = new Random();
        handler = new CharacterEventHandler();
        this.setName("RandomCharacterGene");
    }

    @ThreadConfined(RandomCharacterGenerator.THREAD_NAME)
    public int getPauseTime() {
    	System.out.println("Random.getPauseTime: " + Thread.currentThread());
        return (int) (Math.max(1000, 5000 * random.nextDouble()));
    }

    @ThreadConfined(ThreadName.EDT)
    public void addCharacterListener(CharacterListener cl) {
    	System.out.println("Random.addCharacterListener: " + Thread.currentThread());
        handler.addCharacterListener(cl);
    }

    @ThreadConfined(ThreadName.EDT)
    public void removeCharacterListener(CharacterListener cl) {
        handler.removeCharacterListener(cl);
    }
    
    @ThreadConfined(RandomCharacterGenerator.THREAD_NAME)
    public void nextCharacter() {
    	System.out.println("Random.nextCharacter: " + Thread.currentThread());
        handler.fireNewCharacter(this,
                                (int) chars[random.nextInt(chars.length)]);
    }

    @ThreadConfined(RandomCharacterGenerator.THREAD_NAME)
    public void run() {
    	System.out.println("Random.run: " + Thread.currentThread());
        while (!done) {
            nextCharacter();
            try {
                Thread.sleep(getPauseTime());
            } catch (InterruptedException ie) {
                return;
            }
        }
    }

    @ThreadConfined(ThreadName.EDT)
    public void setDone() {
    	System.out.println("Random.setDone: " + Thread.currentThread());
        done = true;
    }
}
