The following README describes the CheckThread Intellij Plugin.
See LICENSE.txt and THIRD_PARTY_LICENSES.txt for licensing information.

OVERVIEW
The CheckThread Intellij plugin is a wrapper around the CheckThread
static analysis engine. This static analysis engine uses the
Apache Byte Code Engineering Library for traversing Java byte code.
When the user presses the CheckThread Intellij button, all of the
output byte code for the currently selected project is loaded into
CheckThread for analysis. After analysis, the output is rendered into
the Intellij IDE.

KNOWN ISSUES
The plugin currently doesn't work on Mac with JRE 1.5.

NOTEWORTHY JAVA CLASSES
CheckThreadToggleActionSimple.java
This is the main entry point that fires when the user clicks on the toolbar button.

CheckThreadRunner.java
This is the main wrapper around the CheckThread static analysis engine. This fires
once per toolbar button press.
