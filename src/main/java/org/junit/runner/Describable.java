package org.junit.runner;


/**
 * 
 */
public interface Describable {
	/**
	 * @return a {@link Description} showing the tests to be run by the receiver
	 */
	public abstract Description getDescription();
}