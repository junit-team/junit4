package org.junit.experimental.scan;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.CategoryFilter;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

/**
 * Scan a given package seeking tests methods and run its.
 * Additionaly is possible to restrict tests by categories ({@link Categories}
 * 
 * Example:
 * @RunWith(Scan.class)
 * @Package("org.junit.experimental")
 * @IncludeCategory(A.class)
 * public class Foo {
 * }
 * 
 * @author ert
 * @see Categories
 */
public class Scan extends Suite {
	/**
	 * Annotation for setting package name for scanning
	 * @author ert
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Package {
		String value() default "";
	}
	
	public Scan(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(builder, klass, getTestClasses(klass));
		try {
			filter(new CategoryFilter(getIncludedCategory(klass),
					getExcludedCategory(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		}
		assertNoCategorizedDescendentsOfUncategorizeableParents(getDescription());
	}

	protected static Class<?>[] getTestClasses(Class<?> klass) {
		Package packageAnnotation = klass.getAnnotation(Package.class);
		String package_ = packageAnnotation == null ? "" : packageAnnotation.value();
		
		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(package_)).
				filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(package_))).
				setScanners(new MethodAnnotationsScanner()));
		
		
		Set<Method> testMethods = reflections.getMethodsAnnotatedWith(Test.class);
		Set<Class<?>> testClasses = new HashSet<Class<?>>();
		
		for (Method method : testMethods) {
			testClasses.add(method.getDeclaringClass());
		}
		return testClasses.toArray(new Class<?>[0]);
	}
	
	private Class<?> getIncludedCategory(Class<?> klass) {
		IncludeCategory annotation= klass.getAnnotation(IncludeCategory.class);
		return annotation == null ? null : annotation.value();
	}

	private Class<?> getExcludedCategory(Class<?> klass) {
		ExcludeCategory annotation= klass.getAnnotation(ExcludeCategory.class);
		return annotation == null ? null : annotation.value();
	}

	private void assertNoCategorizedDescendentsOfUncategorizeableParents(Description description) throws InitializationError {
		if (!canHaveCategorizedChildren(description))
			assertNoDescendantsHaveCategoryAnnotations(description);
		for (Description each : description.getChildren())
			assertNoCategorizedDescendentsOfUncategorizeableParents(each);
	}
	
	private void assertNoDescendantsHaveCategoryAnnotations(Description description) throws InitializationError {			
		for (Description each : description.getChildren()) {
			if (each.getAnnotation(Category.class) != null)
				throw new InitializationError("Category annotations on Parameterized classes are not supported on individual methods.");
			assertNoDescendantsHaveCategoryAnnotations(each);
		}
	}

	// If children have names like [0], our current magical category code can't determine their
	// parentage.
	private static boolean canHaveCategorizedChildren(Description description) {
		for (Description each : description.getChildren())
			if (each.getTestClass() == null)
				return false;
		return true;
	}
}
