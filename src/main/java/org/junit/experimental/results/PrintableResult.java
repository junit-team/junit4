package org.junit.experimental.results;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * A test result that prints nicely in error messages.
 * This is only intended to be used in JUnit self-tests.
 * For example:
 * 
 * <pre>
 *    assertThat(testResult(HasExpectedException.class), isSuccessful());
 * </pre>
 */
public class PrintableResult {
	/**
	 * The result of running JUnit on {@code type}
	 */
	public static PrintableResult testResult(Class<?> type) {
		return testResult(Request.aClass(type));
	}
	
	/**
	 * The result of running JUnit on Request {@code request}
	 */
	public static PrintableResult testResult(Request request) {
		return new PrintableResult(new JUnitCore().run(request));
	}
	
	private Result result;

	/**
	 * A result that includes the given {@code failures}
	 */
	public PrintableResult(List<Failure> failures) {
		this(new FailureList(failures).result());
	}

	private PrintableResult(Result result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new TextListener(new PrintStream(stream)).testRunFinished(result);
		return stream.toString();
	}

	/**
	 * Returns the number of failures in this result.
	 */
	public int failureCount() {
		return result.getFailures().size();
	}
}