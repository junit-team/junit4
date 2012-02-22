package org.junit.tests.description;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;

public class AnnotatedDescriptionTest {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyOwnAnnotation {

	}

	@MyOwnAnnotation
	public static class AnnotatedClass {
		@Test
		public void a() {
		}
	}

	@Test
	public void annotationsExistOnDescriptionsOfClasses() {
		assertTrue((describe(AnnotatedClass.class).getAnnotation(
				MyOwnAnnotation.class) != null));
	}

	@Test
	public void getAnnotationsReturnsAllAnnotations() {
		assertEquals(1, describe(ValueAnnotatedClass.class).getAnnotations()
				.size());
	}

	@Ignore
	public static class IgnoredClass {
		@Test
		public void a() {
		}
	}

	@Test
	public void annotationsExistOnDescriptionsOfIgnoredClass() {
		assertTrue((describe(IgnoredClass.class).getAnnotation(Ignore.class) != null));
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ValuedAnnotation {
		String value();
	}

	@ValuedAnnotation("hello")
	public static class ValueAnnotatedClass {
		@Test
		public void a() {
		}
	}

	@Test
	public void descriptionOfTestClassHasValuedAnnotation() {
		Description description= describe(ValueAnnotatedClass.class);
		assertEquals("hello", description.getAnnotation(ValuedAnnotation.class)
				.value());
	}

	@Test
	public void childlessCopyOfDescriptionStillHasAnnotations() {
		Description description= describe(ValueAnnotatedClass.class);
		assertEquals("hello", description.childlessCopy().getAnnotation(ValuedAnnotation.class)
				.value());
	}

	@Test
	public void characterizeCreatingMyOwnAnnotation() {
		Annotation annotation= new Ignore() {
			public String value() {
				return "message";
			}

			public Class<? extends Annotation> annotationType() {
				return Ignore.class;
			}
		};

		assertEquals(Ignore.class, annotation.annotationType());
	}

	private Description describe(Class<?> testClass) {
		return Request.aClass(testClass).getRunner().getDescription();
	}
}
