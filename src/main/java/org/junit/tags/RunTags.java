package org.junit.tags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)  //The annotation is saved in the*.class and can be used by the JVM.
@Target(value = ElementType.METHOD)  //The annotation can be used on methods.
public @interface RunTags {
    /**
     * A placeholder to pass an array of all the tags applicable to the JUnit test method
     * @return tags tagged for test method
     */
    String[] tags();       //tag names

    /**
     * @return the reason for ignoring the JUnit test
     */
    String reason() default "Not Specified"; //The optional reason why the test is ignored.
}
