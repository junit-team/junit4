package org.junit.internal.requests;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.CompositeRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ClassesRequest extends Request {
	private final Class<?>[] fClasses;
	private final String fName;
	
	public ClassesRequest(String name, Class<?>... classes) {
		fClasses= classes;
		fName= name;
	}

	/** @inheritDoc */
	@Override 
	public Runner getRunner() {
		List<Runner> runners= new ArrayList<Runner>();
		for (Class<?> each : fClasses) {
			Runner childRunner= Request.aClass(each).getRunner();
			if (childRunner != null) // TODO when can this happen?
				runners.add(childRunner);
		}
		CompositeRunner runner= new CompositeRunner(fName, runners);
		return runner;
	}
}