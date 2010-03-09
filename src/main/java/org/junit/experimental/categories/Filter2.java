/**
 * 
 */
package org.junit.experimental.categories;

import java.util.List;

import org.junit.runner.Runner;

public abstract class Filter2 {
	public abstract List<Runner> matchingRunners(
			List<Runner> allPossibleRunners);
}