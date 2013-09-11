package examples.javathreads.ch02;

import java.util.*;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.EDT)
public class CharacterEventHandler {
    private Vector listeners = new Vector();

    @ThreadSafe
    public void addCharacterListener(CharacterListener cl) {
        listeners.add(cl);
    }

    @ThreadSafe
    public void removeCharacterListener(CharacterListener cl) {
        listeners.remove(cl);
    }

    @ThreadSafe
    public void fireNewCharacter(CharacterSource source, int c) {
        CharacterEvent ce = new CharacterEvent(source, c);
	CharacterListener[] cl = (CharacterListener[] )
				 listeners.toArray(new CharacterListener[0]);
	for (int i = 0; i < cl.length; i++)
	    cl[i].newCharacter(ce);
    }
}
