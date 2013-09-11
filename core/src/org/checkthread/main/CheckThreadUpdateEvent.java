package org.checkthread.main;

/**
 * Event value object
 * Instances of this class are passed to 
 * client plugins (e.g. Eclipse) for displaying to the IDE console window
 *
 */
public class CheckThreadUpdateEvent {
    public enum Type {
    	PROGRESS
    }
    private CheckThreadUpdateEvent.Type fType = Type.PROGRESS;
    private String fMessage;
    
    public CheckThreadUpdateEvent(String msg) {
    	fMessage = msg;
    }
    public CheckThreadUpdateEvent.Type getType() {
    	return fType;
    }
    
    public String getMessage() {
    	return fMessage;
    }
}
