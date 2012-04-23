package org.junit.experimental.scan;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
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
 * 
 * Example:
 * @RunWith(Scan.class)
 * @Package("tests/package")
 * public class Foo {
 * }
 * 
 * @author ert
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
	
	/**
	 * Additional suite for running all test restricted to categories
	 * 
	 * Example:
     * @RunWith(Scan.WithCategories.class)
     * @Package("tests.package")
     * @IncludeCategory(A.class)
     * public class Bar {
     * }
     * 
	 * @author ert
	 * @see Categories
	 *
	 */
	public static class WithCategories extends Categories {
		public WithCategories(Class<?> klass, RunnerBuilder builder) throws InitializationError {
			super(klass, new Scan(klass, builder));
		}
	}
	
	public Scan(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(builder, klass, getTestClasses(klass));
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
}
