package org.junit.runner.notification;

import org.junit.runner.Description;

public class InvalidAssumption extends Failure {
	public InvalidAssumption(Description description, Throwable thrownException) {
		super(description, thrownException);
	}
}
