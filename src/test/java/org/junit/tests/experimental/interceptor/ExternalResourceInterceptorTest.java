package org.junit.tests.experimental.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.interceptor.ExternalResource;
import org.junit.experimental.interceptor.Interceptor;

public class ExternalResourceInterceptorTest {
	private static String callSequence;
	
	public static class UsesExternalResource {
		@Interceptor public ExternalResource resource = new ExternalResource() {
			@Override
			protected void before() throws Throwable {
				callSequence += "before ";
			};
			
			@Override
			protected void after() {
				callSequence += "after ";
			};
		};
		
		@Test public void testFoo() {
			callSequence += "test ";
		}
	}

	@Test public void externalResourceGeneratesCorrectSequence() {
		callSequence= "";
		assertThat(testResult(UsesExternalResource.class), isSuccessful());
		assertEquals("before test after ", callSequence);
	}
}
