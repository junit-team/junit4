package org.junit.internal.requests;

import java.util.Random;

import org.junit.internal.Shuffler;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ShufflingRequest extends Request {
	private final Request fRequest;

	private final Random fRandom;

	public ShufflingRequest(Request request, Random random) {
		fRequest= request;
		fRandom= random;
	}

	@Override
	public Runner getRunner() {
		Runner runner= fRequest.getRunner();
		new Shuffler(fRandom).apply(runner);
		return runner;
	}
}
