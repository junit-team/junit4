package org.mearvk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation which specifies in which order test classes should be run
 * 
 * @see "http://code.google.com/p/junit-test-orderer/"
 * 
 * @author Max Rupplin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassRunOrder
{
	/**
	 * The order the class will be run in.
	 * 
	 * @return The order the class will be run in.
	 */
	int order();
}
