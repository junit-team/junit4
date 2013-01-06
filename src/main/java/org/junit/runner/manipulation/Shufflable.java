package org.junit.runner.manipulation;

import java.util.Random;

/**
 * Interface for runners that allow shuffling of tests.
 */
public interface Shufflable {
	/**
	 * Shuffle the tests using the specified source of randomness. If
	 * <code>random</code> is <code>null</code>, then a default source of
	 * randomness is used.
	 * 
	 * @param random
	 *            the {@link Random} source of randomness
	 */
	public void shuffle(Random random);
}
