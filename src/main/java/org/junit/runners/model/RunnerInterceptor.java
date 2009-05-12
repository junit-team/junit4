package org.junit.runners.model;


public interface RunnerInterceptor {
	void runChild(Runnable childStatement);
	void finished();
}
