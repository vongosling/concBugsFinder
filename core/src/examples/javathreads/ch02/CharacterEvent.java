package examples.javathreads.ch02;

import org.checkthread.annotations.*;

@ThreadSafe
public class CharacterEvent {
    public CharacterSource source;
    public int character;

    public CharacterEvent(CharacterSource cs, int c) {
        source = cs;
	character = c;
    }
}
