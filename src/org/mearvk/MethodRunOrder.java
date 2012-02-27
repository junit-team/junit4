package org.mearvk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation which specifies in which order methods within an annotated (
 * {@code ClassRunOrder}) should be run
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
 * 
 * @author Max Rupplin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodRunOrder
{
	/**
	 * The order the should will run in.
	 * 
	 * @return The order specified in the annotation
	 */
	int order();
}
