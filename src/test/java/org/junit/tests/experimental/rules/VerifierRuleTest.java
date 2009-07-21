package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.rules.ErrorCollector;
import org.junit.rules.Verifier;

public class VerifierRuleTest {
	public static class UsesErrorCollector {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.addError(new Throwable("message"));
		}
	}
	
	@Test public void usedErrorCollectorShouldFail() {
		assertThat(testResult(UsesErrorCollector.class), hasFailureContaining("message"));
	}
	
	public static class UsesErrorCollectorTwice {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.addError(new Throwable("first thing went wrong"));
			collector.addError(new Throwable("second thing went wrong"));
		}
	}
	
	@Test public void usedErrorCollectorTwiceShouldFail() {
		PrintableResult testResult= testResult(UsesErrorCollectorTwice.class);
		assertThat(testResult, hasFailureContaining("first thing went wrong"));
		assertThat(testResult, hasFailureContaining("second thing went wrong"));
	}
	
	public static class UsesErrorCollectorCheckThat {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.checkThat(3, is(4));
			collector.checkThat(5, is(6));
		}
	}
	
	@Test public void usedErrorCollectorCheckThatShouldFail() {
		PrintableResult testResult= testResult(UsesErrorCollectorCheckThat.class);
		assertThat(testResult, hasFailureContaining("got: <3>"));
		assertThat(testResult, hasFailureContaining("got: <5>"));
	}

	public static class UsesErrorCollectorCheckSucceeds {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.checkSucceeds(new Callable<Object>() {
				public Object call() throws Exception {
					throw new RuntimeException("first!");
				}
			});
			collector.checkSucceeds(new Callable<Object>() {
				public Object call() throws Exception {
					throw new RuntimeException("second!");
				}
			});
		}
	}
	
	@Test public void usedErrorCollectorCheckSucceedsShouldFail() {
		PrintableResult testResult= testResult(UsesErrorCollectorCheckSucceeds.class);
		assertThat(testResult, hasFailureContaining("first!"));
		assertThat(testResult, hasFailureContaining("second!"));
	}

	public static class UsesErrorCollectorCheckSucceedsPasses {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			assertEquals(3, collector.checkSucceeds(new Callable<Object>() {
				public Object call() throws Exception {
					return 3;
				}
			}));
		}
	}
	
	@Test public void usedErrorCollectorCheckSucceedsShouldPass() {
		PrintableResult testResult= testResult(UsesErrorCollectorCheckSucceedsPasses.class);
		assertThat(testResult, isSuccessful());
	}
	
	private static String sequence;
	
	public static class UsesVerifier {
		@Rule
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