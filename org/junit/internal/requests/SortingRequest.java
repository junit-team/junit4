package org.junit.internal.requests;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Sorter;

public class SortingRequest extends Request {
	private final Request fRequest;
	private final Comparator<Description> fComparator;

	public SortingRequest(Request request, Comparator<Description> comparator) {
		fRequest= request;
		fComparator= comparator;
	}

	@Override
	public Runner getRunner() {
		Runner runner= fRequest.getRunner();
		new Sorter(fComparator).apply(runner);
		return runner;
	}
}
