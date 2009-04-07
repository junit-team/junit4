package org.junit.experimental.max;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.internal.requests.SortingRequest;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class MaxCore {
	public static MaxCore forFolder(String fileName) {
		return storedLocally(new File(fileName));
	}
	
	public static MaxCore storedLocally(File storedResults) {
		return new MaxCore(storedResults);
	}

	public final MaxHistory fHistory;

	public MaxCore(File storedResults) {
		fHistory = MaxHistory.forFolder(storedResults);
	}

	public Result run(Class<?> testClass) {
		return run(Request.aClass(testClass));
	}

	public Result run(Request request) {
		return run(request, new JUnitCore());
	}

	public Result run(Request request, JUnitCore core) {
		core.addListener(fHistory.listener());
		try { 
			return core.run(sortRequest(request).getRunner());
		} finally {
			try {
				fHistory.save();
			} catch (FileNotFoundException e) {
				// TODO
				e.printStackTrace();
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
	}
	
	// TODO (Feb 23, 2009 10:14:05 PM): publicized for squeeze
	public Request sortRequest(Request request) {
		if (request instanceof SortingRequest) // We'll pay big karma points for this
			return request;
		List<Description> leaves= findLeaves(request);
		Collections.sort(leaves, fHistory.testComparator());
		return constructLeafRequest(leaves);
	}

	// TODO (Feb 23, 2009 10:42:05 PM): V
	public Request constructLeafRequest(List<Description> leaves) {
		final List<Runner> runners = new ArrayList<Runner>();
		for (Description each : leaves)
			runners.add(buildRunner(each));
		return new Request() {
			@Override
			public Runner getRunner() {
				try {
					return new Suite((Class<?>)null, runners) {};
				} catch (InitializationError e) {
					return new ErrorReportingRunner(null, e);
				}
			}
		};
	}

	// TODO (Feb 23, 2009 11:17:01 PM): V
	public Runner buildRunner(Description each) {
		if (each.toString().equals("TestSuite with 0 tests"))
			try {
				// TODO (Nov 18, 2008 2:18:28 PM): move to Suite
				return new Suite(null, new Class<?>[0]);
			} catch (InitializationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Class<?> type= each.getTestClass();
		if (type == null)
			// TODO (Nov 18, 2008 2:04:09 PM): add a check if building a runner is possible
			throw new RuntimeException("Can't build a runner from description [" + each + "]");
		String methodName= each.getMethodName();
		if (methodName == null)
			return Request.aClass(type).getRunner();
		return Request.method(type, methodName).getRunner();
	}

	public List<Description> sortedLeavesForTest(Request request) {
		return findLeaves(sortRequest(request));
	}
	
	// TODO (Feb 23, 2009 10:40:23 PM): V
	public List<Description> findLeaves(Request request) {
		List<Description> results= new ArrayList<Description>();
		findLeaves(request.getRunner().getDescription(), results);
		return results;
	}
	
	// TODO (Feb 23, 2009 10:50:48 PM): V
	public void findLeaves(Description description, List<Description> results) {
		if (description.getChildren().isEmpty())
			results.add(description);
		else
			for (Description each : description.getChildren())
				findLeaves(each, results);
	}
}

