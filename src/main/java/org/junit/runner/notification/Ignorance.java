package org.junit.runner.notification;

import org.junit.runner.Description;

public class Ignorance extends Failure {
	public Ignorance(Description description, Throwable thrownException) {
		super(description, thrownException);
	}
}
