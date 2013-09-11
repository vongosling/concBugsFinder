/*
Copyright (c) 2008 Joe Conti, CheckThread.org

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package org.checkthread.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtil {
	
	// helper function
	// returns a JarEntry array sorted so that enclosing classes
	// are the beginning and inner classes are at the end of the array
	public static JarEntry[] getSortedJarEntries(JarFile jarFile) {
		
		Enumeration <JarEntry> e = jarFile.entries();
        ArrayList<JarEntry> list = new ArrayList<JarEntry>();
        while(e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            list.add(jarEntry);   
        }        
        
		JarEntry[] sortedJarEntries = new JarEntry[list.size()];
		
		int outerClassInd = 0;
		int innerClassInd = list.size()-1;
		for(JarEntry jarEntry : list) {
		    if(jarEntry.getName().contains("$")) {
		    	sortedJarEntries[innerClassInd--] = jarEntry;
		    } else {
		    	sortedJarEntries[outerClassInd++] = jarEntry;
		    }
		}
		return sortedJarEntries;
	}
}
