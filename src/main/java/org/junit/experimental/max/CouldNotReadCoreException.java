package org.junit.experimental.max;

/**
 * Thrown when Max cannot read the MaxCore serialization
 * @since 4.6
 */
public class CouldNotReadCoreException extends Exception {
	private static final long serialVersionUID= 1L;

	/**
	 * Constructs
	 */
	public CouldNotReadCoreException(Throwable e) {
		super(e);
	}
}
