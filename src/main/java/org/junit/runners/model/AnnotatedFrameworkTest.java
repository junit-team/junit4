package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.rules.TestRule;
import org.junit.runner.Description;

public class AnnotatedFrameworkTest implements FrameworkTest {

	protected final TestClass testClass;

	public final FrameworkMethod method;

	private Class<? extends Throwable> expectedException;

	private long timeout;

	private boolean ignored;

	public AnnotatedFrameworkTest(TestClass testClass, FrameworkMethod method) {
		this.testClass= testClass;
		this.method= method;

		processAnnotations();
	}

	void processAnnotations() {
		Test testAnnotation= getAnnotation(Test.class);

		expectedException= testAnnotation.expected();
		if (expectedException == None.class) {
			expectedException= null;
		}

		timeout= testAnnotation.timeout();

		ignored= getAnnotation(Ignore.class) != null;
	}

	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return method.getAnnotation(annotationType);
	}

	public Class<? extends Throwable> getExpectedException() {
		return expectedException;
	}

	public void setExpectedException(
			Class<? extends Throwable> expectedException) {
		this.expectedException= expectedException;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout= timeout;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setIgnored(boolean ignored) {
		this.ignored= ignored;
	}

	public Description createDescription() {
		return Description.createTestDescription(testClass.getJavaClass(),
				method.getName(), method.getAnnotations());
	}

	public Statement createStatement(Object test, List<TestRule> testRules) {
		Statement statement= methodInvoker(test);
		statement= possiblyExpectingExceptions(statement);
		statement= withPotentialTimeout(statement);
		statement= withTestRules(testRules, statement);
		return statement;
	}

	//
	// Statement builders
	//

	/**
	 * Returns a {@link Statement} that invokes {@code method} on {@code test}
	 */
	protected Statement methodInvoker(Object test) {
		return new InvokeMethod(method, test);
	}

	protected Statement possiblyExpectingExceptions(Statement statement) {
		if (expectedException != null) {
			statement= new ExpectException(statement, expectedException);
		}
		return statement;
	}

	protected Statement withPotentialTimeout(Statement statement) {
		if (timeout > 0) {
			statement= new FailOnTimeout(statement, timeout);
		}
		return statement;
	}

	protected Statement withTestRules(List<TestRule> testRules,
			Statement statement) {
		for (TestRule testRule : testRules) {
			statement= testRule.apply(statement, createDescription());
		}
		return statement;
	}
}
