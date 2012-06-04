package org.junit.tests.experimental.max;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;

public class DescriptionTest {

	@Test
	public void parseClass_whenCantParse() {
		assertNull(Description.TEST_MECHANISM.getTestClass());
	}

	@Test
	public void parseMethod_whenCantParse() {
		assertNull(Description.TEST_MECHANISM.getMethodName());
	}

	@Test(expected= IllegalArgumentException.class)
	public void createSuiteDescription_whenZeroLength() {
		Description.createSuiteDescription("");
	}

	@Test(expected= IllegalArgumentException.class)
	public void createSuiteDescription_whenNull() {
		Description.createSuiteDescription((String) null);
	}

	@Test
	public void parseClassAndMethodNoAnnotations() throws Exception {
		Description description = Description.createTestDescription(Description.class, "aTestMethod");

		assertThat(description.getClassName(), equalTo("org.junit.runner.Description"));
		assertThat(description.getMethodName(), equalTo("aTestMethod"));
		assertThat(description.getAnnotations().size(), equalTo(0));
	}

	@Test
	public void parseClassAndMethodWithAnnotations() throws Exception {
		Annotation[] annotations = DescriptionTest.class.getMethod("parseClassAndMethodWithAnnotations").getDeclaredAnnotations();

		Description description = Description.createTestDescription(Description.class, "aTestMethod", annotations);

		assertThat(description.getClassName(), equalTo("org.junit.runner.Description"));
		assertThat(description.getMethodName(), equalTo("aTestMethod"));
		assertThat(description.getAnnotations().size(), equalTo(1));
	}

	@Test
	public void parseClassNameAndMethodUniqueId() throws Exception {
		Description description = Description.createTestDescription("something that's not a class name", "aTestMethod", 123);

		assertThat(description.getClassName(), equalTo("something that's not a class name"));
		assertThat(description.getMethodName(), equalTo("aTestMethod"));
		assertThat(description.getAnnotations().size(), equalTo(0));
	}

	@Test
	public void sameNamesButDifferentUniqueIdAreNotEqual() throws Exception {
		assertThat(Description.createTestDescription("something that's not a class name", "aTestMethod", 1),
						not(equalTo(Description.createTestDescription("something that's not a class name", "aTestMethod", 2))));
	}
}
