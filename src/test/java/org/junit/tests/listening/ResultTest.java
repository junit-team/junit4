package org.junit.tests.listening;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class ResultTest {
	@Test public void failedAssumptionDoesntCountAsRun() throws Exception {
		Result result= new Result();
		RunListener listener= result.createListener();
		Description someTest= Description.EMPTY;
		listener.testStarted(someTest);
		listener.testAssumptionFailed(someTest, null);
		listener.testFinished(someTest);
		assertThat(result.getRunCount(), is(0));
	}
}
