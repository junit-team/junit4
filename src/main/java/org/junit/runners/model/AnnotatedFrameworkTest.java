package org.junit.runners.model;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.Description;

public class AnnotatedFrameworkTest implements FrameworkTest {

	private final FrameworkMethod method;

	public AnnotatedFrameworkTest(FrameworkMethod method) {
		this.method= method;
	}

	public Description createDescription(TestClass testClass) {
		return Description.createTestDescription(testClass.getJavaClass(),
				method.getName(), method.getAnnotations());
	}

	public boolean shouldBeIgnored() {
		return method.getAnnotation(Ignore.class) != null;
	}

	public Statement createStatement(Object test) {
		Test testAnnotation= method.getAnnotation(Test.class);
		Statement statement= methodInvoker(method, test);
		statement= possiblyExpectingExceptions(testAnnotation, statement);
		statement= withPotentialTimeout(testAnnotation, statement);
		return statement;
	}

	//
	// Statement builders
	//

	/**
	 * Returns a {@link Statement} that invokes {@code method} on {@code test}
	 */
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	private Statement possiblyExpectingExceptions(Test testAnnotation,
			Statement statement) {
		Class<? extends Throwable> expectedException= testAnnotation.expected();
		if (expectedException != null && expectedException != None.class) {
			statement= new ExpectException(statement, expectedException);
		}
		return statement;
	}

	private Statement withPotentialTimeout(Test testAnnotation,
			Statement statement) {
		long timeout= testAnnotation.timeout();
		if (timeout > 0) {
			statement= new FailOnTimeout(statement, timeout);
		}
		return statement;
	}
}
