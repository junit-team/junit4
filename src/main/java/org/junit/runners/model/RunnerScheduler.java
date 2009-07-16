package org.junit.runners.model;


public interface RunnerScheduler {
	void schedule(Runnable childStatement);
	void finished();
}
