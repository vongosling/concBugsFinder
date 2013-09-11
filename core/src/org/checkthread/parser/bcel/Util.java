/*
Copyright (c) 2008 Joe Conti CheckThread.org

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

package org.checkthread.parser.bcel;

import java.lang.reflect.*;
import java.util.regex.*;

import org.checkthread.config.*;

final public class Util {
   
    private final static String REGEX = "\\w*";
    private final static Pattern sPattern = Pattern.compile(REGEX);
  
    private static String getFirstWord(String instr) {
        String retval = null;
        Matcher matcher = sPattern.matcher(instr);
        if (matcher.find()) {
            String str = matcher.group();
            retval = str;
        }
        retval = retval.trim();
        return retval;
    }

    public static String cleanMethod(String methodSyntax) throws Exception 
    {
        String retval = null;
        
        if(methodSyntax.startsWith(ByteCodeConstants.CONSTRUCTOR_INDENTIFIER)) {
            return ByteCodeConstants.CONSTRUCTOR_INDENTIFIER;
        } else if (methodSyntax.startsWith(ByteCodeConstants.STATICBLOCK_IDENTIFIER)) {
			return ByteCodeConstants.STATICBLOCK_IDENTIFIER;
		} else {
			if (methodSyntax.startsWith("access$")) {
				
				int ind = methodSyntax.indexOf(" (");
				if(ind>0) {
				    retval = methodSyntax.substring(0, ind).trim();
				} else {
					retval = methodSyntax.trim();
				}
			} else {
				retval = getFirstWord(methodSyntax);
				if (retval == null || retval.length() < 1) {
					throw new Exception("Could not process input: "
							+ methodSyntax);
				}
			}
			return retval;
		}
	}
    
    private static String cleanClassName(String className) {
        className = className.replace("/",".");
        
        Class cls = null;
        // If className doesn't have package prefixed
        // e.g. Object, MyClassWithNoPackage, etc
        if (!className.contains(".")) {
        	
        	// see if this class can be loaded
        	try {
        	    cls = ClassLoaderBridge.loadClass(className);
        	} catch(ClassNotFoundException e) {}
        	
        	// try prefixing java.lang
        	// This code path occurs when className=="Object"
        	String classNameMod = "java.lang." + className;
        	try {
        	    cls = ClassLoaderBridge.loadClass(classNameMod);
        	    className = classNameMod;
        	} catch(ClassNotFoundException e) {}
        	
        	// give up
        	if(cls==null) {
        		Log.severe("ClassLoader Can't load: " + className);
        	}     	
        }
        return className;
    }
    
    public static Class loadClass(String className) {
        Class cls = null;
        try {
            className = cleanClassName(className);
            cls = ClassLoaderBridge.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }
    
    public static AccessibleObject loadMethod(
            String className,
            String methodName,
            String[] methodArgs,
            boolean isSynthetic) {
        
    	Log.logByteInfo("Util");
    	Log.logByteInfo("loadMethod: " + methodName);
    	
    	if(methodName.length()<1) {
            Log.severe("Util.loadMethod: Empty Method");
        }
        
        AccessibleObject method = null;

		try {
			methodName = cleanMethod(methodName);
		} catch (Exception e) {
			e.printStackTrace();
		}
   
        
        className = cleanClassName(className);
        Class cls = null;
        try {
        	cls = ClassLoaderBridge.loadClass(className);
        } catch (ClassNotFoundException e) {
        	Log.reportException(e);
        	Log.severe("Invalid class name: " + className);
        }
         
        if(cls!=null) {            
            Class[] classArgs = new Class[methodArgs.length];
            for (int n = 0; n<methodArgs.length; n++) {
                classArgs[n] = loadMethodArg(methodArgs[n]);
            }
            method = loadMethodHelper(cls,methodName,classArgs);
        }
        
        return method;
    }
    
    private static Class loadMethodArg(String className) {
        Class cls = null;
        className = className.replace('/','.');
        try {
            if(className.equals("int")) {
                cls = Integer.TYPE;
            } else if (className.equals("int[]")) {
                cls = Class.forName("[I");
            } else if (className.equals("char")) {
                cls = Character.TYPE;
            } else if (className.equals("char[]")) {
                cls = Class.forName("[C");
            } else if (className.equals("double")) {
                cls = Double.TYPE;
            } else if (className.equals("double[]")) {
                cls = Class.forName("[D");
            } else if (className.equals("boolean")) {
                cls = Boolean.TYPE;
            } else if (className.equals("boolean[]")) {
                cls = Class.forName("[Z");
            } else if (className.equals("short")) {
                cls = Short.TYPE;
            } else if (className.equals("short[]")) {
                cls = Class.forName("[S");
            } else if (className.equals("byte")) {
                cls = Byte.TYPE;
            } else if (className.equals("byte[]")) {
                cls = Class.forName("[B");
            } else if (className.equals("long")) {
                cls = Long.TYPE;
            } else if (className.equals("long[]")) {
                cls = Class.forName("[J");
            } else if (className.equals("float")) {
                cls = Float.TYPE;
            } else if (className.equals("float[]")) {
                cls = Class.forName("[F");
            } else if (className.endsWith("[]")) {
                className = "[L"+className.substring(0,className.length()-2)+";";
            }
            if (cls==null) {
                cls = ClassLoaderBridge.loadClass(className);
            }
        } catch (Exception e) {
        	// synthetic class
        }

        return cls;
    }

    public static AccessibleObject loadMethodHelper(
            Class cls,
            String methodName,
            Class[] args) {
        AccessibleObject method = null;
       
        if(methodName.length()<1) {
            Log.severe("Empty Method");
        }
        
        try {
            if (methodName.equals(ByteCodeConstants.CONSTRUCTOR_INDENTIFIER)) {
                try {
                    method = cls.getConstructor(args);
                } catch (Exception e) {
                    method = cls.getDeclaredConstructor(args);
                }
            } else {
                try {
                    method = cls.getMethod(methodName, args);
                } catch (NoSuchMethodException e) {
                    method = cls.getDeclaredMethod(methodName, args);
                } catch (NoClassDefFoundError ef) {
                	Log.reportError("Class: " + cls);
                	for(Class clazz : args) {
                		Log.reportError("Args: " + clazz);
                	}
                	Log.reportError(ef.getMessage());
                }
            }
        } catch (Exception e) {
            // try super class
            if (cls.getSuperclass()!=null) {
                return loadMethodHelper(cls.getSuperclass(),methodName,args);
            } else {
            	// heuristic
            	if (cls==Object.class) {
            		if(methodName.trim().equals("<init>")) {
            		    // no op
            		} else {
            		    //out.println("Class: " + cls + " Method: " + methodName);
            		}
            	} else {
            	    //out.println("ERROR: " + cls);
            		Log.reportError("EXCEPTION: " + e);
            		//e.printStackTrace();
            	}
            }
        }
        
        return method;
    }
    
    public static AccessibleObject loadField(
            String className,
            String fieldName) {
        AccessibleObject field = null;
        fieldName = getFirstWord(fieldName);
        try {
            Class cls = ClassLoaderBridge.loadClass(className);
            field = loadFieldHelper(cls, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }
    
    private static AccessibleObject loadFieldHelper(
            Class cls,
            String fieldName) {
        AccessibleObject field = null;
        
        //TBD
        if(fieldName.equals("this")) {
            return null;
        }
        
        try {
            try {
                field = cls.getField(fieldName);
            } catch (NoSuchFieldException e) {
            	try {
                    field = cls.getDeclaredField(fieldName);
                    
                 // give up
            	} catch(Exception e2) {
            		// heuristic, ignore ENUM
            		if(!fieldName.equals("ENUM")) {
            		    //out.println("Problem with class: " + cls + " " + fieldName);
            		}
            	}
            }
        } catch (Exception e) {
            // try super class
            if (cls.getSuperclass()!=null) {
                return loadFieldHelper(cls.getSuperclass(),fieldName);
            } else {
            	//ToDo: heuristic, ignore "val" for now
            	if(!fieldName.equals("val")) {
                    e.printStackTrace();
            	}
            }
        }
        return field;
    }
    
    // For testing
    public static void main(String [] args) throws Exception {
        Class cls = ClassLoaderBridge.loadClass("org.checkthread.test.target.swing.TestSwingExample2");
        if(cls!=null) {
        	Method[] mlist = cls.getMethods();
        	for(Method m : mlist) {
        		System.out.println(m.getName());
        	}
        	
        	System.out.println("Declared");
        	mlist = cls.getDeclaredMethods();
        	for(Method m : mlist) {
        		Log.logByteInfo(m.toGenericString());
        	}
        }
    }
    
}
