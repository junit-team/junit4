package org.junit.tests.experimental.interceptor;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.interceptor.ExpectedException;
import org.junit.experimental.interceptor.Rule;

public class ExpectedExceptionInterceptorTest {
	public static class HasExpectedException {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsNothing() {

		}

		@Test
		public void throwsNullPointerException() {
			thrown.expect(NullPointerException.class);
			throw new NullPointerException();
		}

		@Test
		public void throwsNullPointerExceptionWithMessage() {
			thrown.expect(NullPointerException.class);
			thrown.expectMessage("happened?");
			throw new NullPointerException("What happened?");
		}
	}

	@Test
	public void expectedExceptionPasses() {
		assertThat(testResult(HasExpectedException.class), isSuccessful());
	}

	public static class HasWrongExpectedException {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsNullPointerException() {
			thrown.expect(NullPointerException.class);
			throw new IllegalArgumentException();
		}
	}

	@Test
	public void unExpectedExceptionFails() {
		assertThat(
				testResult(HasWrongExpectedException.class),
				hasSingleFailureContaining("Expected: an instance of java.lang.NullPointerException"));
	}

	public static class HasWrongMessage {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsNullPointerException() {
			thrown.expectMessage("expectedMessage");
			throw new IllegalArgumentException("actualMessage");
		}
	}

	@Test
	public void wrongMessageFails() {
		assertThat(
				testResult(HasWrongMessage.class),
				hasSingleFailureContaining("\"expectedMessage\"\n     but: getMessage() was \"actualMessage\""));
	}

	public static class WronglyExpectsException {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void doesntThrowNullPointerException() {
			thrown.expect(NullPointerException.class);
		}
	}

	@Test
	public void failsIfExceptionNeverComes() {
		assertThat(
				testResult(WronglyExpectsException.class),
				hasSingleFailureContaining("Expected test to throw an instance of java.lang.NullPointerException"));
	}

	public static class WronglyExpectsExceptionMessage {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void doesntThrowAnything() {
			thrown.expectMessage("anything!");
		}
	}

	@Test
	public void failsIfExceptionMessageNeverComes() {
		assertThat(
				testResult(WronglyExpectsExceptionMessage.class),
				hasSingleFailureContaining("Expected test to throw exception with message a string containing \"anything!\""));
	}

	public static class ExpectsSubstring {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expectMessage("anything!");
			throw new NullPointerException(
					"This could throw anything! (as long as it has the right substring)");
		}
	}

	@Test
	public void passesWithSubstringMethod() {
		assertThat(testResult(ExpectsSubstring.class), isSuccessful());
	}

	public static class ExpectsSubstringNullMessage {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expectMessage("anything!");
			throw new NullPointerException();
		}
	}

	@Test
	public void failsWithNullExceptionMessage() {
		assertThat(testResult(ExpectsSubstringNullMessage.class),
				hasSingleFailureContaining("but: getMessage() was null"));
	}

	public static class ExpectsMessageMatcher {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expectMessage(startsWith("Ack"));
			throw new NullPointerException("Ack!");
		}
	}

	@Test
	public void succeedsWithMessageMatcher() {
		assertThat(testResult(ExpectsMessageMatcher.class), isSuccessful());
	}

	public static class ExpectedMessageMatcherFails {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expectMessage(startsWith("Wrong start"));
			throw new NullPointerException("Back!");
		}
	}

	@Test
	public void failsWithMatcher() {
		assertThat(testResult(ExpectedMessageMatcherFails.class),
				hasSingleFailureContaining("Wrong start"));
	}

	public static class ExpectsMatcher {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expect(any(Throwable.class));
			throw new NullPointerException("Ack!");
		}
	}

	@Test
	public void succeedsWithMatcher() {
		assertThat(testResult(ExpectsMatcher.class), isSuccessful());
	}

	public static class ExpectsMultipleMatchers {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsMore() {
			thrown.expect(IllegalArgumentException.class);
			thrown.expectMessage("Ack!");
			throw new NullPointerException("Ack!");
		}
	}

	@Test
	public void failsWithMultipleMatchers() {
		assertThat(testResult(ExpectsMultipleMatchers.class),
				hasSingleFailureContaining("IllegalArgumentException"));
	}
}
