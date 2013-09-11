package org.checkthread.test.target.threadconfined.swing;

import javax.swing.*;
import java.awt.*;

import org.checkthread.annotations.*;

@ThreadSafe
public class TestSwingAPI {
    
   public void method1() {
       JButton jButton = null;
       jButton.setBackground(Color.RED); // ERROR, EDT thread violation
       jButton.setBounds(null); // okay, java.awt method
       jButton.setVisible(false); //ERROR, EDT thread violation
   }
    
   public void mymethod2() {
	   Button button = null;
	   button.setBounds(null); // okay, awt
	   button.setBackground(Color.RED); // okay, awt
	   button.setEnabled(true); // okay, awt
   } 
}