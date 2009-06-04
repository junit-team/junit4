package org.junit.tests.experimental.interceptor;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.interceptor.ErrorCollector;
import org.junit.experimental.interceptor.Interceptor;
import org.junit.experimental.interceptor.Verifier;

public class VerifierInterceptorTest {
	public static class UsesErrorCollector {
		@Interceptor
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.addError(new Throwable("message"));
		}
	}
	
	@Test public void usedErrorCollectorShouldFail() {
		assertThat(testResult(UsesErrorCollector.class), hasFailureContaining("message"));
	}
	
	public static class UsesErrorCollectorTwice {
		@Interceptor
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.addError(new Throwable("first thing went wrong"));
			collector.addError(new Throwable("second thing went wrong"));
		}
	}
	
	@Test public void usedErrorCollectorTwiceShouldFail() {
		assertThat(testResult(UsesErrorCollectorTwice.class), hasFailureContaining("first thing went wrong"));
		assertThat(testResult(UsesErrorCollectorTwice.class), hasFailureContaining("second thing went wrong"));
	}
	
	private static String sequence;
	
	public static class UsesVerifier {
		@Interceptor
		public Verifier collector= new Verifier() {
			@Override
			public void verify() {
				sequence+= "verify ";
			}
		};
		
		@Test public void example() {
			sequence+= "test ";
		}
	}
	
	@Test public void verifierRunsAfterTest() {
		assertThat(testResult(UsesVerifier.class), isSuccessful());
	}
}
