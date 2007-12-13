package org.junit.runner.notification;

import org.junit.runner.Description;

public class FailedAssumption extends Failure {
	public FailedAssumption(Description description, Throwable thrownException) {
		super(description, thrownException);
	}
}
