package org.checkthread.test.target.customannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class TestCustomAnnotation {

	// Custom "NotThreadSafe" annotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface NotThreadSafe {}
	
	// Custom "ThreadSafe" annotation
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface ThreadSafe {}
	
	@ThreadSafe
	public void foo() {
        // ERROR: Calling custom "NotThreadSafe" annotation
		bar();
		
		// OKAY
		foofoo();
	}
	
	@NotThreadSafe
	public void bar() {
		System.out.println("barbar");
	}
	
	@ThreadSafe
	public void foofoo() {
		System.out.println("foofoo");
	}
}
