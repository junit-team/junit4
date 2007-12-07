package org.junit.runner.notification;

import org.junit.runner.Description;

// TODO: (Dec 7, 2007 11:17:10 AM) Check structure

public class Ignorance extends Failure {
	public Ignorance(Description description, Throwable thrownException) {
		super(description, thrownException);
	}
}
