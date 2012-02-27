package org.mearvk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation which specifies in which order test classes should be run
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
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
