package org.junit.tests.validation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class FailedConstructionTest {
	public static class CantConstruct {
		public CantConstruct() {
			throw new RuntimeException();
		}

		@Test
		public void foo() {
		}
	}

	@Test
	public void failedConstructionIsTestFailure() {
		Result result= JUnitCore.runClasses(CantConstruct.class);
		Failure failure= result.getFailures().get(0);
		Description expected= Description.createTestDescription(CantConstruct.class, "foo");
		Assert.assertEquals(expected, failure.getDescription());
	}
}
