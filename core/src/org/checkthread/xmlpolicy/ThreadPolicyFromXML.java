/*
Copyright (c) 2009 Joe Conti, CheckThread.org

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

package org.checkthread.xmlpolicy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.checkthread.main.CheckThreadUpdateEvent;
import org.checkthread.main.ICheckThreadListener;
import org.checkthread.policy.*;
import org.checkthread.util.*;
import org.checkthread.annotations.*;
import org.checkthread.config.ClassLoaderBridge;
import org.checkthread.config.ConfigBean;
import org.checkthread.config.ConfigSingletonFactory;
import org.checkthread.config.Log;
import org.checkthread.policy.IThreadPolicy;
import org.checkthread.policy.ThreadConfinedPolicy;

public class ThreadPolicyFromXML {

	private final static String FILE_NAME = "threadpolicy.xml";
	private final static String VERSION = "1.0";
	private static HashMap<String,Boolean> policyFileSearchCache = new HashMap<String,Boolean>();
	private static HashMap<String,ClassEntryDO> classMap = new HashMap<String, ClassEntryDO>();
	private static ArrayList<ClassPatternEntryDO> classPatternList = new ArrayList<ClassPatternEntryDO>();
	private static HashMap<String,Pattern> patternMap = new HashMap<String,Pattern>();
	
	// search cache for performance
	private static HashMap<AccessibleObject,IThreadPolicy> threadPolicySearchMapCache = new HashMap<AccessibleObject,IThreadPolicy>();
	private static HashMap<AccessibleObject,Boolean> threadNullPolicySearchMapCache = new HashMap<AccessibleObject,Boolean>();
	
	private static boolean doInit = true;
	
    public static void main(String[] args) throws Exception {
		lazyLoad();
    }
    
    public static void clearCache() {
    	classMap.clear();
    	classPatternList.clear();
    	patternMap.clear();
    	threadPolicySearchMapCache.clear();
    	threadNullPolicySearchMapCache.clear();
    	policyFileSearchCache.clear();
    	doInit = true;
    }
    
    private static void lazyLoad() {
		if (doInit) {
			doInit = false;
			try {
				ConfigBean configBean = ConfigSingletonFactory.getConfigBean();
				ICheckThreadListener listener = configBean.getListener();
				
				// load from isolated class loader (for loading custom threadpolicy.xml)
				ClassLoader cloader = ClassLoaderBridge.getIsolatedClassLoader();			
				loadXMLFromClassLoader(cloader,listener);
				
				// load from primary class loader (for loading checkthread's built-in threadpolicy.xml)
				cloader = ClassLoaderBridge.getClassLoader();			
				loadXMLFromClassLoader(cloader,listener);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}   
    
    // helper for loading threadpolicy.xml from a ClassLoader
    private static void loadXMLFromClassLoader(ClassLoader cloader, ICheckThreadListener listener) throws Exception{
    	Enumeration<URL> e = cloader.getResources(FILE_NAME);	
		while (e.hasMoreElements()) {

			URL url = e.nextElement();
			String path = url.toExternalForm();

			// If we haven't already loaded this xml file
			if (policyFileSearchCache.get(path) == null) {

				// notify listener
				String msg = "Loading " + url.toExternalForm();
				if (listener != null) {
					CheckThreadUpdateEvent evt = new CheckThreadUpdateEvent(msg);
					listener.analyzeUpdate(evt);
				}

				// load in XML descriptor
				loadXMLFile(url, listener);
				
				// store path so we don't load it again
				policyFileSearchCache.put(path, Boolean.TRUE);
			}
		}	
    }

    // load in checkthread.xml file
    private static void loadXMLFile(URL xmlFile, ICheckThreadListener listener) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlFile.toExternalForm());
			doc.getDocumentElement().normalize();
			
			String version = doc.getDocumentElement().getAttribute("version");
			if(version!=null && version.equals(VERSION)) {
				loadVerZeroDotZeroXMLFile(doc);			
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
			/*
			// for debugging 
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bos);
			e.printStackTrace(ps);
			ps.close();
			try {
			    bos.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
			String stackStr = bos.toString();
			*/
			
			// notify listener 
			String msg = "Error reading: " + xmlFile.toString() + "\n" + e.getMessage();
			if(listener!=null) {
				CheckThreadUpdateEvent evt = new CheckThreadUpdateEvent(msg);
				listener.analyzeUpdate(evt);
			}
			Log.severe(e.getMessage());
		}
    }
    
    // load version 0.0 threadpolicy.xml file
    private static void loadVerZeroDotZeroXMLFile(Document doc) {
    	NodeList classList = doc.getElementsByTagName("class");
    	handleClassList(classList);
	} 
    
    private static void handleClassList(NodeList classList) {
    	
    	// loop through class elements
		for (int s = 0; s < classList.getLength(); s++) {
			Node classNode = classList.item(s);	
			Node classNameNode = classNode.getAttributes().getNamedItem("name");
			String className = null;
			String patternClassName = null;
			
			// class node specifies "name"
			if(classNameNode!=null) {
				className = classNameNode.getNodeValue();
				
		    // class node specifies "pattern"
			} else  {
				Node patternNameNode = classNode.getAttributes().getNamedItem("pattern");
				if(patternNameNode!=null) {
					patternClassName = patternNameNode.getNodeValue();	
				}
			}

			if (classNode instanceof Element) {

				if (className != null) {
					try {
						Class targetClass = ClassLoaderBridge.loadClass(className);
						ClassEntryDO value = new ClassEntryDO(targetClass);
						classMap.put(className, value);
						NodeList methodList = ((Element) classNode).getElementsByTagName("policy");
						handleThreadPolicyList(value, methodList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else if (patternClassName != null) {
					try {
						ClassPatternEntryDO value = new ClassPatternEntryDO(patternClassName);
						classPatternList.add(value);
						NodeList methodList = ((Element) classNode).getElementsByTagName("policy");
						handleThreadPolicyList(value, methodList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

    private static void handleThreadPolicyList(IMethodEntryProvider entry, NodeList policyList) {
    	
		// loop through thread policies
		for (int s = 0; s < policyList.getLength(); s++) {
			Node threadPolicyNode = policyList.item(s);
			try {
                handleThreadPolicyNode(entry,threadPolicyNode);
			} catch(Exception e) {
				System.err.println("Error reading xml");
			}
		}
    }
    
    // return true if the node descriptor suppresses checkthread errors
    private static boolean doSuppressErrors(Node threadPolicyNode) {
    	boolean isSuppressErrors = false;
		Node node = threadPolicyNode.getAttributes().getNamedItem("suppresserrors");
		if(node!=null && node.getNodeValue().equals("true")) {
		    isSuppressErrors = true;	
		}
		return isSuppressErrors;
    }
    
    // handle thread policy descriptor node
    private static void handleThreadPolicyNode(IMethodEntryProvider entry, Node threadPolicyNode) {
		String policyType = threadPolicyNode.getAttributes().getNamedItem("type").getNodeValue();
		boolean isSuppressErrors = doSuppressErrors(threadPolicyNode);
		
		// get thread policy for the supplied node
		IThreadPolicy policy = null;
		if (policyType.equals("ThreadConfined")) {
			String confinedName = threadPolicyNode.getAttributes().getNamedItem("threadname").getNodeValue();
			boolean isImplicit = true;
			confinedName = ThreadName.getThreadNameFromXMLName(confinedName);
			policy = new ThreadConfinedPolicy(isSuppressErrors, confinedName,isImplicit);
		} else if (policyType.equals("ThreadSafe")) {
			policy = new ThreadSafePolicy(isSuppressErrors);
		} else if (policyType.equals("NotThreadSafe")) {
			policy = new NotThreadSafePolicy(Scope.UNDEFINED,isSuppressErrors);
		}
						
		if(policy!=null) {
			if(threadPolicyNode instanceof Element) {
			   NodeList methodList = ((Element) threadPolicyNode).getElementsByTagName("method");
			   handleMethodList(entry, policy, methodList);
			}
		}	
    }
     
    // handle method xml descriptor node
    private static void handleMethodList(IMethodEntryProvider entry,
    		IThreadPolicy policy,
    		NodeList methodList) {
    	
    	ArrayList<String> methodNameList = new ArrayList<String>();
    	
    	// Loop through method elements
    	for(int s = 0; s<methodList.getLength(); s++) {
    		Node methodNode = methodList.item(s);
    		String methodName =  methodNode.getAttributes().getNamedItem("pattern").getNodeValue();
    		methodNameList.add(methodName);
    	}
    	
    	// create method value object
    	MethodEntryDO methodEntry = new MethodEntryDO();
    	methodEntry.setMethodNameList(methodNameList);
    	methodEntry.setThreadPolicy(policy);
    	entry.addMethodEntry(methodEntry);
    }
    
    private static boolean isRegExMatch(String strToAnalyze, String regex) {
    	boolean retval = false;
    	
    	Pattern pattern = patternMap.get(regex);
    	
    	// create pattern if there isn't one already
    	if(pattern==null) {
    	    pattern = Pattern.compile(regex);
    	    patternMap.put(regex,pattern);
    	}
    	
    	// execute regular expression
        Matcher matcher = pattern.matcher(strToAnalyze);
        if (matcher.matches()) {
        	retval = true;
        }
    	return retval;
    }
    
    private static IThreadPolicy getThreadPolicyFromEntry(String methodToAnalyze,
    		                                                     Class ownerClass,
    		                                                     IMethodEntryProvider entry) {
    	IThreadPolicy retval = null;
		
		// loop through thread policies
		ArrayList<MethodEntryDO> methodList = entry.getMethodList();
	    for(MethodEntryDO methodEntry : methodList) {

	    	// loop through method names
	    	ArrayList<String> methodNameList = methodEntry.getMethodNameList();
	    	
	    	for(String methodName : methodNameList) {
				if(isRegExMatch(methodToAnalyze,methodName)) {	
					Method[] methodRefList = ownerClass.getDeclaredMethods();
					for(Method m : methodRefList) {
						if(m.getName().equals(methodToAnalyze)) {	
						    retval = methodEntry.getThreadPolicy();
							break;		
						}
					}

				}
	    	}   	    	
	    }
    	return retval;
    }
    
	public static IThreadPolicy getThreadPolicyFromXML(String XXXclassName, AccessibleObject obj) {
		lazyLoad();
		IThreadPolicy policy = null;
	    
		// fast return if already searched
		policy = threadPolicySearchMapCache.get(obj);
	    if(policy!=null) {
			return policy;
		}
	    
	    // fast return if already searched and returned null
	    if(threadNullPolicySearchMapCache.get(obj) instanceof Boolean) {
	    	return null;
	    }
	    
	    // Perform search
	    String methodToAnalyze = ReflectUtil.getName(obj);
		Class declaringClass = ReflectUtil.getDeclaringClass(obj);
        String className = declaringClass.getName();
        ClassEntryDO classInfo = null;
        
        try {
    	    Class classToAnalyze = ClassLoaderBridge.loadClass(className);
    	    Class clazz = classToAnalyze;
    	    while(clazz!=null) {
    	    	
    	    	// check class
    	    	classInfo = classMap.get(clazz.getName());
    	    	if(classInfo!=null) {
    	    		policy = getThreadPolicyFromEntry(methodToAnalyze,declaringClass,classInfo);
    	    		if(policy!=null) {
    	    			break;
    	    		}
    	    		
    	        // check interfaces
    	    	} else {
    	    		Class[] interfaceList = clazz.getInterfaces();
    	    		for(Class cls : interfaceList) {
    	    			classInfo = classMap.get(cls.getName());
    	    			if(classInfo!=null) {
    	    				policy = getThreadPolicyFromEntry(methodToAnalyze,declaringClass,classInfo);
    	    				if(policy!=null) {
    	    				   break;
    	    				}
    	    			}
    	    		}
    	    	}
    	    	
    	    	// go up the hierarchy
    	    	clazz = clazz.getSuperclass();	
    	    } // while
    	    
    	    // if policy is still null, see if any class pattern name
			// descriptors specify a policy
			if (policy == null) {
				for (ClassPatternEntryDO entry : classPatternList) {
					String classNamePattern = entry.getClassNamePattern();
					String candidateClassName = declaringClass.getName();
					if (isRegExMatch(candidateClassName, classNamePattern)) {
						policy = getThreadPolicyFromEntry(methodToAnalyze,declaringClass, entry);
						if (policy != null) {
							break;
						}
					}
				}

			}
        
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        // cache for performance 
        if(policy!=null) {
            threadPolicySearchMapCache.put(obj,policy);
        } else {
        	threadNullPolicySearchMapCache.put(obj,Boolean.TRUE);
        }
        
        return policy;
	}
}
