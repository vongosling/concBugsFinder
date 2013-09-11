/* From http://java.sun.com/docs/books/tutorial/index.html */
/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package examples.thread.example3;

import org.checkthread.annotations.*;

@ThreadConfined(ThreadName.MAIN)
public class ThreadRace {

  private final static int NUMRUNNERS = 2;
  
  public static void main(String[] args) {
	  System.out.println("Start");
    SelfishRunner[] runners = new SelfishRunner[NUMRUNNERS];

    for (int i = 0; i < NUMRUNNERS; i++) {
      runners[i] = new SelfishRunner(i);
      runners[i].setPriority(2);
    }
    for (int i = 0; i < NUMRUNNERS; i++)
      runners[i].start();
  }
}

@ThreadConfined(ThreadName.MAIN)
class SelfishRunner extends Thread {
  public final static String THREADID = "SelfishThread";
  
  private int tick;
  private int num;
  
  public SelfishRunner(int num) {
    this.num = num;
    tick = 1;
  }

  public void force_data_race() {
	  tick = 4;
  }
  
  @ThreadConfined(SelfishRunner.THREADID)
  public void run() {
    while (tick < 400000) {
      tick++;
      if ((tick % 50000) == 0)
        System.out.println("Thread #" + num + ", tick = " + tick);
    }
  }
}
