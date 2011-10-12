/**
 * 
 */
package org.junit.experimental.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import static java.util.Collections.addAll;
import static java.util.Collections.unmodifiableSet;
import static org.junit.runner.Description.createSuiteDescription;

/**
 * From a given set of test classes, runs only the classes and methods that are
 * annotated with either the category given with the @IncludeCategory
 * annotation, or a subtype of that category.
 * 
 * Note that, for now, annotating suites with {@code @Category} has no effect.
 * Categories must be annotated on the direct method or class.
 * <p>
 * Two system properties override categories declared by {@link IncludeCategory}
 * and {@link ExcludeCategory}. Every of these two properties use a comma separated
 * list of file names of categories which are Java types, used as follows:
 * <p><blockquote><pre>
 * -Dorg.junit.categories.included=com/Category1.java,com/Category2.java
 * -Dorg.junit.categories.excluded=com/Category3.java,com/Category4.java
 * </pre></blockquote>
 * <p>
 * Thus a class 'com.Category1' has file name 'com/Category1.java'.
 * Malformed value or an unknown category ({@link ClassNotFoundException}) in these
 * properties cause thrown {@link InitializationError} in this constructor.
 * This finally determines Included/Excluded categories applied to the suite:
 * <ul>
 *  <li> Included are those categories which are contained in the non-empty list of
 *       'org.junit.categories.included' and are assignable to {@link IncludeCategory};
 *       or possibly no categories after this intersection;
 *  <li> Excluded are all those which are declared by {@link ExcludeCategory} on the
 *       type of suite, and in the list of categories in 'org.junit.categories.excluded'.
 * </ul>
 * <p>
 * Empty or nonexistent properties 'org.junit.categories.included' and
 * 'org.junit.categories.excluded' do not modify the categories {@link IncludeCategory}
 * and {@link ExcludeCategory}, respectively.
 * 
 * Example:
 * 
 * <pre>
 * public interface FastTests {
 * }
 * 	
 * public interface SlowTests {
 * }
 * 
 * public static class A {
 * 	&#064;Test
 * 	public void a() {
 * 		fail();
 * 	}
 * 
 * 	&#064;Category(SlowTests.class)
 * 	&#064;Test
 * 	public void b() {
 * 	}
 * }
 * 
 * &#064;Category( { SlowTests.class, FastTests.class })
 * public static class B {
 * 	&#064;Test
 * 	public void c() {
 * 
 * 	}
 * }
 * 
 * &#064;RunWith(Categories.class)
 * &#064;IncludeCategory(SlowTests.class)
 * &#064;SuiteClasses( { A.class, B.class })
 * // Note that Categories is a kind of Suite
 * public static class SlowTestSuite {
 * }
 * </pre>
 * @see {@link Class#isAssignableFrom(Class)}
 */
public class Categories extends Suite {
	// the way filters are implemented makes this unnecessarily complicated,
	// buggy, and difficult to specify.  A new way of handling filters could
	// someday enable a better new implementation.
        // https://github.com/KentBeck/junit/issues/issue/172
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<?> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExcludeCategory {
		public Class<?> value();
	}

	public static class CategoryFilter extends Filter {
        private static final Class<?>[] NO_CATEGORIES = new Class<?>[0];

		public static CategoryFilter include(Class<?> categoryType) {
			return new CategoryFilter(categoryType, null);
		}

        /**
         * TODO useful method for maven-surefire-plugin
         * public static createCategoryFilter(Set<Class<?>> includes, Set<Class<?>> excludes):CategoryFilter
         * TODO performance improvement includes.removeAll(excludes) unless any null, and use them both anyway
         * See the issue #336
         * https://github.com/KentBeck/junit/issues/336
         * public static CategoryFilter createCategoryFilter(Set<Class<?>> includes, Set<Class<?>> excludes) {
         *      return new CategoryFilter(includes, excludes);
         * }
         * */

		private final Set<Class<?>> fIncluded,//NULL represents 'All' categories without limitation to Included.
                                    fExcluded;//Cannot be null. Empty Set does not exclude categories.

		public CategoryFilter(final Class<?> includedCategory, final Class<?> excludedCategory) {
            this(copyAndRefine(includedCategory, null), copyAndRefine(excludedCategory, Collections.<Class<?>>emptySet()));
		}

		public CategoryFilter(final Collection<Class<?>> includes, final Collection<Class<?>> excludes) {
            this(copyAndRefine(includes, null), copyAndRefine(excludes, Collections.<Class<?>>emptySet()));
		}

		private CategoryFilter(final Set<Class<?>> includes, final Set<Class<?>> excludes) {
			fIncluded = includes == null ? null : unmodifiableSet(includes);
			fExcluded = excludes == null ? Collections.<Class<?>>emptySet() : unmodifiableSet(excludes);
		}

		@Override
		public String describe() {
            return toString();
		}

        @Override public String toString() {
            final StringBuilder description = new StringBuilder(128);
            if (fIncluded == null)
                description.append("categories ")
                        .append(fExcluded.isEmpty() ? "[all]" : "[[all]");
            else if (fIncluded.size() == 1)
                description.append("category ")
                        .append(fExcluded.isEmpty() ? "" : "[")
                        .append(fIncluded.iterator().next());
            else description.append("categories ")
                        .append(fExcluded.isEmpty() ? "" : "[")
                        .append(fIncluded.toString());
            if (!fExcluded.isEmpty())
                description.append(" - ")
                        .append(fExcluded.toString())
                        .append(']');
			return description.toString();
        }

		@Override
		public boolean shouldRun(Description description) {
			if (hasCorrectCategoryAnnotation(description))
				return true;
			for (Description each : description.getChildren())
				if (shouldRun(each))
					return true;
			return false;
		}

		private boolean hasCorrectCategoryAnnotation(Description description) {
			final Set<Class<?>> categories = categories(description);
			if (categories.isEmpty()) return fIncluded == null;
			for (final Class<?> each : categories)
				if (hasAssignableFrom(fExcluded, each))
					return false;
			for (final Class<?> each : categories)
				if (fIncluded == null
					|| hasAssignableFrom(fIncluded, each))
					return true;
			return false;
		}

        private static Set<Class<?>> copyAndRefine(final Class<?> clazz, final Set<Class<?>> fallback) {
            return clazz == null ? fallback : singleton(clazz);
        }

        private static Set<Class<?>> copyAndRefine(final Collection<Class<?>> classes, final Set<Class<?>> fallback) {
            if (classes == null || classes.isEmpty()) return fallback;
            final Set<Class<?>> c = new HashSet<Class<?>>(classes);
            return c.remove(null) && c.isEmpty() ? fallback : c;
        }

		private static Set<Class<?>> categories(Description description) {
			Set<Class<?>> categories = new HashSet<Class<?>>();
			addAll(categories, directCategories(description));
            addAll(categories, directCategories(parentDescription(description)));
			return categories;
		}

		private static Description parentDescription(Description description) {
			Class<?> testClass = description.getTestClass();
			if (testClass == null) return null;
			return createSuiteDescription(testClass);
		}

		private static Class<?>[] directCategories(Description description) {
			if (description == null) return NO_CATEGORIES;
			final Category annotation = description.getAnnotation(Category.class);
			if (annotation == null) return NO_CATEGORIES;
			return annotation.value();
		}
	}

	public Categories(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(klass, builder);
		try {
			filter(new CategoryFilter(getIncludedCategory(klass), getExcludedCategory(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		} catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
        assertNoCategorizedDescendentsOfUncategorizeableParents(getDescription());
	}

	private static Set<Class<?>> getIncludedCategory(Class<?> klass) throws ClassNotFoundException {
		IncludeCategory annotation = klass.getAnnotation(IncludeCategory.class);//FIX for issue #336: IncludeCategory[] annotations
        // see https://github.com/KentBeck/junit/issues/336
		return intersectWithSystemPropertyInclusions(annotation == null ? null : createSet(annotation.value()));
	}

	private static Set<Class<?>> getExcludedCategory(Class<?> klass) throws ClassNotFoundException {
		ExcludeCategory annotation = klass.getAnnotation(ExcludeCategory.class);//FIX for issue #336: ExcludeCategory[] annotations
        // see https://github.com/KentBeck/junit/issues/336
		return unionWithSystemPropertyExclusions(annotation == null ? createSet() : createSet(annotation.value()));
	}

    private static Set<Class<?>> intersectWithSystemPropertyInclusions(Set<Class<?>> includes) throws ClassNotFoundException {
        final Class<?>[] extCategories = getCategoriesBySystemProperty("org.junit.categories.included");
        if (includes == null || includes.remove(null) && includes.isEmpty()) {///ready for plural in IncludeCategory
            if (extCategories.length == 0) return null;//included categories are all
            if (includes == null) includes = new HashSet<Class<?>>(extCategories.length);
            addAll(includes, extCategories);
            return includes;//we have some categories from external system property 'org.junit.categories.included'
        } else if (extCategories.length == 0) return includes;
        final Set<Class<?>> subCategories = new HashSet<Class<?>>();
        for (final Class<?> extCategory : extCategories)
            if (hasAssignableFrom(includes, extCategory)) subCategories.add(extCategory);
        return subCategories;//if empty, nothing to test
    }

    private static Set<Class<?>> unionWithSystemPropertyExclusions(Set<Class<?>> excludes) throws ClassNotFoundException {
		excludes.remove(null);///ready for plural in ExcludeCategory
        addAll(excludes, getCategoriesBySystemProperty("org.junit.categories.excluded"));
        return excludes;//if empty union, nothing to exclude
    }

    private static Class<?>[] getCategoriesBySystemProperty(final String systemPropertyKey) throws ClassNotFoundException {
        final List<Class<?>> categories = new ArrayList<Class<?>>();
        for (String clazz : System.getProperty(systemPropertyKey, "").split(",")) {
            clazz = clazz.trim();
            if (clazz.length() != 0) {
                if (clazz.length() <= ".java".length()
                        || !clazz.regionMatches(true, clazz.length() - ".java".length(), ".java", 0, ".java".length()))
                    throw new ClassNotFoundException("file name must be finished by \".java\" ignoring case");
                clazz = clazz.substring(0, clazz.length() - ".java".length());
                if (clazz.indexOf('.') != -1)
                    throw new ClassNotFoundException("File name must not contain '.'. Instead use slash '/'.");
                clazz = clazz.replace('/', '.');
                categories.add(Class.forName(clazz));
            }
        }
        return categories.toArray(new Class[categories.size()]);
    }

	private static void assertNoCategorizedDescendentsOfUncategorizeableParents(Description description) throws InitializationError {
		if (!canHaveCategorizedChildren(description))
			assertNoDescendantsHaveCategoryAnnotations(description);
		for (Description each : description.getChildren())
			assertNoCategorizedDescendentsOfUncategorizeableParents(each);
	}
	
	private static void assertNoDescendantsHaveCategoryAnnotations(Description description) throws InitializationError {
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

    private static boolean hasAssignableFrom(Set<Class<?>> assigns, Class<?> from) {
        for (final Class<?> assign : assigns)
            if (assign.isAssignableFrom(from)) return true;
        return false;
    }

    private static Set<Class<?>> createSet(Class<?>... t) {
        final Set<Class<?>> set = new HashSet<Class<?>>();
        addAll(set, t);
        return set;
    }

    private static Set<Class<?>> singleton(Class<?> o) {
        final Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(o);
        return set;
    }
}