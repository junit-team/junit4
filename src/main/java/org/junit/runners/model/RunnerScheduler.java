package org.junit.runners.model;

/**
 * Represents a strategy for scheduling when individual test methods
 * should be run (in serial or parallel)
 * 
 * WARNING: still experimental, may go away.
 */
public interface RunnerScheduler {
	/**
	 * Schedule a child statement to run
	 */
	void schedule(Runnable childStatement);
	
	/**
	 * Override to implement any behavior that must occur
	 * after all children have been scheduled (for example,
	 * waiting for them all to finish)
	 */
	void finished();
}
