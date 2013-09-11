/*

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

package org.checkthread.annotations;

/**
 * Helper class for predefined thread confined.
 */
public class ThreadName {
	final public static String MAIN = "org.checkthread.annotations.ThreadName.MAIN";
	final public static String EDT = "org.checkthread.annotations.ThreadName.EDT";
	final public static String SWT_UI = "org.checkthread.annotations.ThreadName.SWT_UI";
	final public static String FINALIZE = "org.checkthread.annotations.ThreadName.FINALIZE";

	/**
	 * Used by XML descriptor infrastructure
	 * @param prettyName
	 * @return
	 */
	public static String getThreadNameFromXMLName(String prettyName) {
		if(prettyName.equals("ThreadName.MAIN")) {
		   return MAIN;	
		} else if(prettyName.equals("ThreadName.EDT")) {
			return EDT;
		} else if (prettyName.equals("ThreadName.SWT_UI")) {
			return SWT_UI;
		} else if (prettyName.equals("ThreadName.FINALIZE")) {
			return FINALIZE;
		} else {
			return null;
		}
	}
	
	public static String getPrettyName(String rawThreadName) {
		if (rawThreadName.equals(MAIN)) {
			return "MAIN";
		} else if (rawThreadName.equals(EDT)) {
			return "EDT";
		} else if (rawThreadName.equals(SWT_UI)) {
			return "SWT_UI";
		} else if (rawThreadName.equals(FINALIZE)) {
			return "FINALIZE";
		} else {
			return rawThreadName;
		}
	}

}