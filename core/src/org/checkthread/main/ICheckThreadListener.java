package org.checkthread.main;

/**
 * Client code should implement this listener to receive
 * callbacks from the CheckThread static analysis engine
 */
public interface ICheckThreadListener {
   public void analyzeUpdate(CheckThreadUpdateEvent evt);
}
