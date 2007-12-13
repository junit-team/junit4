/**
 * 
 */
package org.junit.experimental.results;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class PrintableResult {
	public static PrintableResult testResult(Class<?> type) {
		return new PrintableResult(type);
	}
	
	private Result result;

	public PrintableResult(List<Failure> failures) {
		this(new FailureList(failures).result());
	}

	public PrintableResult(Result result) {
		this.result = result;
	}
	
	public PrintableResult(Class<?> type) {
		this(JUnitCore.runClasses(type));
	}

	@Override
	public String toString() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		new TextListener(new PrintStream(stream)).testRunFinished(result);
		return stream.toString();
	}

	public List<Failure> getFailures() {
		return result.getFailures();
	}
}