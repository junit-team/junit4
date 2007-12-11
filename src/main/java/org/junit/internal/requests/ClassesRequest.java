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
			runners.add(childRunner);  // TODO David, I took out the null check after examining all the implementors of getRunner()
		}
		CompositeRunner runner= new CompositeRunner(fName, runners);
		return runner;
	}
}