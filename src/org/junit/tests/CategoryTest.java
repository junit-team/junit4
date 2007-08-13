package org.junit.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;

import org.junit.Test;
import org.junit.runner.Category;
import org.junit.runner.CategoryFilter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

public class CategoryTest {
	// TODO: (Aug 6, 2007 3:59:37 PM) Common base class?

	public static class InternetConnected {

	}

	public static class UserAvailable {
	}

	public static class SomeUITests {
		@Category(UserAvailable.class)
		@Test
		public void askUserToPressAKey() {

		}

		@Test
		public void simulatePressingKey() {

		}
	}
	
	// TODO: (Aug 6, 2007 5:20:47 PM) Should be able to add multiple categories with varargs
	// TODO: (Aug 6, 2007 5:21:10 PM) Method should have union of class and method categories.
	// TODO: (Aug 6, 2007 5:21:54 PM) Request.inCategories should take varargs: the union of all categories




	@Category(InternetConnected.class)
	public static class InternetTests {
		@Test
		public void pingServer() {

		}
	}

	// public static class UserAvailableSuite {
	// @Request public Request request() {
	// return
	// Request.aClass(SomeUITests.class).inCategories(UserAvailable.class);
	// }
	// }

	@Test
	public void runACategory() {
		Request request= Request.aClass(SomeUITests.class).inCategories(
				UserAvailable.class);
		assertThat(request.getRunner().testCount(), is(1));

		Result result= new JUnitCore().run(request);
		assertThat(result.getRunCount(), is(1));
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void describeCategoryFilter() {
		assertThat(new CategoryFilter(UserAvailable.class).describe(),
				is("in category UserAvailable"));
	}

	@Test
	public void categoryAnnotationOnAClass() {
		Request request= Request.classes("all my tests", SomeUITests.class,
				InternetTests.class).inCategories(InternetConnected.class);
		assertThat(request.getRunner().testCount(), is(1));

		Result result= new JUnitCore().run(request);
		assertThat(result.getRunCount(), is(1));
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void categoryFilterOnMethodWithAnnotatedClass()
			throws SecurityException, NoSuchMethodException {
		Annotation[] annotations= InternetTests.class.getMethod("pingServer")
				.getAnnotations();
		Description description= Description.createTestDescription(InternetTests.class, "pingServer",
				annotations);
		assertTrue(new CategoryFilter(InternetConnected.class).shouldRun(description));
	}
}
