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

package org.checkthread.policy;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import org.checkthread.annotations.*;
import org.checkthread.util.*;
import org.checkthread.xmlpolicy.ThreadPolicyFromXML;

public class PolicyFactory {

	private static HashMap<AccessibleObject,IThreadPolicy> sCache = new HashMap<AccessibleObject,IThreadPolicy>();
	private static HashMap<AccessibleObject,Boolean> sNotCache = new HashMap<AccessibleObject, Boolean>();
	
	// called by CacheManager
	public static void clearCache() {
		sCache.clear();
		sNotCache.clear();
	}
	
	public static IThreadPolicy getDerivedThreadPolicy(String derivedClassName, AccessibleObject obj) {
		IThreadPolicy policy = null;
		
		AccessibleObject derivedObj = ReflectUtil.getDerivedMethod(derivedClassName,obj);
		if(derivedObj!=null) {
			policy = getThreadPolicy(derivedObj);
		} else {
			policy = getThreadPolicy(derivedObj);
		}
		return policy;
	}
	
    public static IThreadPolicy getThreadPolicy(AccessibleObject obj) {
    	return getThreadPolicy(null,obj);
    }
    
    // main entry point: determine policy for a obj
    public static IThreadPolicy getThreadPolicy(String className, AccessibleObject obj) {
        IThreadPolicy policy = null;
    	if(obj==null) {
            return null;
        }
    	
    	// short circuit if already checked
    	Boolean b = sNotCache.get(obj);
    	if(b!=null && b==true) {
    		return null;
    	}
    	
    	// short circuit if already checked
    	policy = sCache.get(obj);
    	if(policy!=null) {
    		return policy;
    	}
    	
    	Class cls = ReflectUtil.getDeclaringClass(obj);
    	
    	// get policy from method
        policy = getThreadPolicyForMethod(obj);

    	// get policy from class
        if(policy==null) {
            policy = getThreadPolicyForClassAll(cls);
        }
        
		// get policy from parent class, traverse up call stack
		if (policy==null) {
		    Class parentClass = cls.getEnclosingClass();
		    while(policy==null && parentClass!=null) {
		    	policy = getThreadPolicyForClassAll(parentClass);
		    	parentClass = parentClass.getEnclosingClass();
		    }
		}
        
        // load policy from XML
        if(policy==null) {
           policy = ThreadPolicyFromXML.getThreadPolicyFromXML(className,obj);	
        }
        
        // save to cache
        if(policy==null) {
        	sNotCache.put(obj,true);
        } else {
        	sCache.put(obj,policy);
        }
        
        return policy;
    }
    
    public static boolean isMethodFinalize(AccessibleObject method) {
    	boolean retval = false;
    	
    	if(method instanceof java.lang.reflect.Method) {
    		java.lang.reflect.Method m = (java.lang.reflect.Method)method;
    		retval = m.getName().equals("finalize");
    	}
    	
    	return retval;
    }
   
    private static IThreadPolicy getThreadPolicyForClass(Class cls) {
        return  getThreadPolicyFromAnnotation(cls.getDeclaredAnnotations());
    }
   
    private static IThreadPolicy getThreadPolicyForClassAll(Class cls) {
    	IThreadPolicy policy = null;
    	
        // get policy from class
        policy = getThreadPolicyForClass(cls);
        
        // get policy from enclosing constructor
		if (policy == null) {
			Constructor enclosingConstructor = cls.getEnclosingConstructor();
			if (enclosingConstructor != null) {
				 policy = getThreadPolicyForMethod(enclosingConstructor);
			}
		}
        
        // get policy from enclosing method
		if (policy == null) {
			Method enclosingMethod = cls.getEnclosingMethod();
			if (enclosingMethod != null) {
				 policy = getThreadPolicyForMethod(enclosingMethod);
			}
		}
		
		return policy;
    }
    
    private static IThreadPolicy getThreadPolicyForMethod(AccessibleObject obj) {
        Annotation [] alist = obj.getDeclaredAnnotations();
        IThreadPolicy policy = getThreadPolicyFromAnnotation(alist);
        return policy;
    }
    
    private static IThreadPolicy getThreadPolicyFromAnnotation(
            Annotation[] alist) {
        IThreadPolicy policy = null;
        for (Annotation a : alist) {
            
        	// CheckThread annotations
            if(a instanceof ThreadConfined) {
            	ThreadConfined t = (ThreadConfined)a;
            	boolean isImplicit = false;
            	policy = new ThreadConfinedPolicy(t,isImplicit);
            } else if(a instanceof ThreadSafe) {
            	ThreadSafe t = (ThreadSafe)a;
            	policy = new ThreadSafePolicy(t);
            } else if(a instanceof NotThreadSafe) {
            	NotThreadSafe t = (NotThreadSafe)a;
            	policy = new NotThreadSafePolicy(t);
            	
            // for reverse compatibility from 1.0.8
            } else if(a instanceof ThreadUnsafe) {
            	policy = new NotThreadSafePolicy(Scope.UNDEFINED,false);	
            
            // support for any annotation named "NotThreadSafe"
            } else if (a.annotationType().getSimpleName().equals("NotThreadSafe")) {
            	policy = new NotThreadSafePolicy(Scope.UNDEFINED,false);
            	
            // support for any annotation named "ThreadSafe"
            } else if (a.annotationType().getSimpleName().equals("ThreadSafe")) {
            	policy = new ThreadSafePolicy(false);
            } 
            
        }
        return policy;
    }
}

