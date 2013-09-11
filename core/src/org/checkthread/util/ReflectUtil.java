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

import java.lang.reflect.*;
import org.checkthread.config.*;
import org.checkthread.parser.bcel.*;

/**
 * Misc utilities related to class reflection
 */
public class ReflectUtil {

   public static String getName(AccessibleObject obj) {
       String retval = null;  
	   if(obj==null) {
		   Log.severe("getName doesn't accept null inputs");
		   return retval;
	   }
        if(obj instanceof Method) {
            Method method = (Method)obj;
            retval = method.getName();
        } else if (obj instanceof Field) {
            Field field = (Field)obj;
            retval = field.getName(); 
        } else {
            Constructor constr = (Constructor)obj;
            retval = constr.getName();
        } 
        return retval;
   }
   
   public static AccessibleObject getDerivedMethod(String derivedClassName, AccessibleObject superMethod) {
		AccessibleObject retval = null;
		Class cls = null;
		
		try {
			cls = ClassLoaderBridge.loadClass(derivedClassName);
		} catch (Exception e) {}
		
		if (cls != null) {
			if (superMethod instanceof Method) {
				Method method = (Method) superMethod;
				Class[] args = method.getParameterTypes();
				retval = Util.loadMethodHelper(cls, method.getName(), args);
			}
		}
		
		return retval;
	}
   
   // Get the class object for a method/field/constructor
   public static Class getDeclaringClass(AccessibleObject obj) {
        Class declaringClass = null;
        if(obj instanceof Method) {
            declaringClass = ((Method)obj).getDeclaringClass();
        } else if (obj instanceof Field) {
            declaringClass = ((Field)obj).getDeclaringClass();
        } else if (obj instanceof Constructor) {
            declaringClass = ((Constructor)obj).getDeclaringClass();
        }
        return declaringClass;
    }
   
   
   public static void main(String [] args) throws Exception {
	   String str = "asdf";
	   Class clazz= java.io.StringReader.class;
	   Method method = clazz.getMethod("read",java.nio.CharBuffer.class);
	   Method superMethod = getClosestSuperMethod(clazz,method);
	   System.out.println("Super method: " + superMethod);
   }
   
   public static Method getClosestSuperMethod(Class clazz, Method method) {
       Method retval = null;
       if(clazz==null) {
    	   Log.severe("ReflectUtil, Null input");
    	   return retval;
       }
       
       Class[] interfaceList = clazz.getInterfaces();
       for(Class cls : interfaceList) {
    	   try {
    	       retval = cls.getMethod(method.getName(),method.getParameterTypes());
    	       break;
    	   } catch(Exception e) {}
       }
       
       Class superClass = clazz.getSuperclass();
       if(retval==null) {
           try {
        	   retval = superClass.getMethod(method.getName(),method.getParameterTypes());   
           } catch (Exception e) {}
       }
       
       // Recurse up to super class
       if(retval==null && superClass!=null) {
    	   retval = getClosestSuperMethod(superClass,method);
       }
       
       return retval;
   }
   
   public static Constructor getClosestSuperConstructor(Class clazz, Constructor c) {
       Constructor retval = null;
       
       if(clazz==null || c==null) {
    	   Log.severe("ReflectUtil, null inputs");
    	   return retval;
       }
       Class superClass = clazz.getSuperclass();
       if(retval==null) {
           try {
        	   retval = superClass.getConstructor(c.getParameterTypes());   
           } catch (Exception e) {}
       }
       
       // Recurse up to super class
       if(retval==null && superClass!=null) {
    	   retval = getClosestSuperConstructor(superClass,c);
       }
       
       return retval;
   }
}
