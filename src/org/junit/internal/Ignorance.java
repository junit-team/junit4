package org.junit.internal;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

// TODO: (Nov 26, 2007 2:38:40 PM) Check location and package size

public class Ignorance extends Failure {
	public Ignorance(Description description, Throwable thrownException) {
		super(description, thrownException);
	}
}
