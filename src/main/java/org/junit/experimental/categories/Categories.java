/**
 * 
 */
package org.junit.experimental.categories;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

/**
 * From a given set of test classes, runs only the classes and methods that are
 * annotated with either the category given with the @IncludeCategory
 * annotation, or a subtype of that category.
 * <p>
 * Note that, for now, annotating suites with {@code @Category} has no effect.
 * Categories must be annotated on the direct method or class.
 * <p>
 * When filtering every individual test method to run, the child categories are
 * those which are declared altogether on the particular test method and test
 * class via {@link Category}.
 * <p>
 * Static parent categories are declared on a suite using {@link IncludeCategory}
 * and/or {@link ExcludeCategory}. Both annotations have a declarative method
 * <em>assignedTo</em> with a specific selection of parent's categories
 * {@link Selection#ANY} or {@link Selection#ALL}.
 * <p>
 * First the runner excludes test methods from running where (ANY or ALL)
 * <em>exclusive</em> parent categories are assignable from child categories.
 * Then the remaining candidate is a subject to run if and only if (ANY or ALL)
 * <em>inclusive</em> parent categories are assignable from child categories.
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
    private static final Selection DEFAULT_IN_SELECT= Selection.ALL;
    private static final Selection DEFAULT_EX_SELECT= Selection.ANY;
	// the way filters are implemented makes this unnecessarily complicated,
	// buggy, and difficult to specify.  A new way of handling filters could
	// someday enable a better new implementation.
        // https://github.com/KentBeck/junit/issues/issue/172
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<?>[] value();
		public Selection assignableTo() default Selection.ALL;///DEFAULT_IN_SELECT;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExcludeCategory {
		public Class<?>[] value();
		public Selection assignableTo() default Selection.ANY;///DEFAULT_EX_SELECT;
	}

	public static class CategoryFilter extends Filter {
        	private static final Class<?>[] NO_CATEGORIES= new Class<?>[0];

		public static CategoryFilter include(Class<?> categoryType) {
			return new CategoryFilter(createSet(categoryType), null);
		}

		public static CategoryFilter include(Selection s, Class<?>... categoryType) {
			return new CategoryFilter(createSet(categoryType), s, null, null);
		}

		public static CategoryFilter include(Class<?>... categoryType) {
			return new CategoryFilter(createSet(categoryType), null);
		}

		public static CategoryFilter exclude(Class<?> categoryType) {
			return new CategoryFilter(null, createSet(categoryType));
		}

		public static CategoryFilter exclude(Class<?>... categoryType) {
			return new CategoryFilter(null, createSet(categoryType));
		}

		public static CategoryFilter categoryFilter(Set<Class<?>> inclusions, Selection inclusionsSelect,
                                                    Set<Class<?>> exclusions, Selection exclusionsSelect) {
			return new CategoryFilter(inclusions, inclusionsSelect, exclusions, exclusionsSelect);
		}

		private final Set<Class<?>> fIncluded,//NULL represents 'All' categories without limitation to Included.
                                    fExcluded;//Cannot be null. Empty Set does not exclude categories.

		private final Selection fIncludedSelect, fExcludedSelect;

		public CategoryFilter(Class<?> includedCategory, Class<?> excludedCategory) {
			this(copyAndRefine(includedCategory, null), copyAndRefine(excludedCategory, Collections.<Class<?>>emptySet()));
		}

		public CategoryFilter(Collection<Class<?>> includes, Collection<Class<?>> excludes) {
			this(copyAndRefine(includes, null), DEFAULT_IN_SELECT, copyAndRefine(excludes, Collections.<Class<?>>emptySet()), DEFAULT_EX_SELECT);
		}

		public CategoryFilter(Collection<Class<?>> includes, Selection includesSelect, Collection<Class<?>> excludes, Selection excludesSelect) {
			this(copyAndRefine(includes, null), includesSelect, copyAndRefine(excludes, Collections.<Class<?>>emptySet()), excludesSelect);
		}

		private CategoryFilter(final Set<Class<?>> includes, Selection includesSelect, final Set<Class<?>> excludes, Selection excludesSelect) {
			if (includesSelect == null) includesSelect = DEFAULT_IN_SELECT;
			if (excludesSelect == null) excludesSelect = DEFAULT_EX_SELECT;
			fIncluded= includes == null || includes.isEmpty() ? null : Collections.unmodifiableSet(includes);
			if (fIncluded == null && includesSelect == DEFAULT_IN_SELECT)
			    throw new IllegalArgumentException(DEFAULT_IN_SELECT + " must have any included categories specified in parent");
			fExcluded= excludes == null ? Collections.<Class<?>>emptySet() : Collections.unmodifiableSet(excludes);
			fIncludedSelect= includesSelect;
			fExcludedSelect= excludesSelect;
		}

		@Override
		public String describe() {
			return toString();
		}

        /**
         * Returns string representation for the relative complement of excluded categories set in the set of
         * included categories.
         * If no excluded categories are specified and only one included category presents, this method returns
         * a name of included category via {@link Class#toString()} (backward compatible). More generally the
         * method returns string in the form "[[<included_categories>] - [<excluded_categories>]]", where both
         * <em>included_categories</em> and <em>excluded_categories</em> are comma separated names.
         * @return string representation for the relative complement of excluded categories set in the set of
         *          included categories. As examples of the categories sets complement:
         * <ul>
         *  <li> "categories [all]" for all included categories and no excluded ones;
         *  <li> "categories [[all] - A]" for all included categories and one excluded category specified;
         *  <li> "categories [[all] - [A, B]]" for all included categories and given excluded ones;
         *  <li> "category A" for one included category and no excluded ones;
         *  <li> "category [A - B]" for one included category and one excluded category specified;
         *  <li> "category [A - [B, C]]" for one included category and given excluded ones;
         *  <li> "categories [[A, B] - C]" for given included categories and one excluded category specified;
         *  <li> "categories [[A, B] - [C, D, E]]" for given included categories and given excluded ones.
         * </ul>
         * <p>
         * @see Class#toString() name of category
         */
        @Override public String toString() {
            final StringBuilder description= new StringBuilder(128);
            final boolean hasExcluded = !fExcluded.isEmpty();
            if (fIncluded == null) description.append("categories ")
                    .append(hasExcluded ? "[[all]" : "[all]");
            else if (fIncluded.size() == 1) description.append("category ")
                    .append(hasExcluded ? "[" : "")
                    .append(fIncluded.iterator().next());
            else description.append("categories ")
                        .append(hasExcluded ? "[" : "")
                        .append(fIncluded.toString());

            if (hasExcluded) description.append(" - ")
                        .append(fExcluded.size() > 1 ? fExcluded.toString() : fExcluded.iterator().next())
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
			final Set<Class<?>> childCategories= categories(description);
			if (childCategories.isEmpty()) return fIncluded == null;

			boolean isExcluded= false, isAll= fExcludedSelect == Selection.ALL;
			for (final Class<?> excluded : fExcluded) {
			    isExcluded = hasAssignableTo(childCategories, excluded);
			    if (isExcluded ^ isAll) {
			        if (isAll) break;
			        return false;
			    }
			}

			if (isAll && isExcluded && !fExcluded.isEmpty()) return false;
			if (fIncluded == null) return true;

			isAll= fIncludedSelect == Selection.ALL;
			for (final Class<?> included : fIncluded)
			    if (hasAssignableTo(childCategories, included) ^ isAll)
			        return !isAll;

			return isAll;
		}

		private static Set<Class<?>> categories(Description description) {
			Set<Class<?>> categories= new HashSet<Class<?>>();
			Collections.addAll(categories, directCategories(description));
			Collections.addAll(categories, directCategories(parentDescription(description)));
			return categories;
		}

		private static Description parentDescription(Description description) {
			Class<?> testClass= description.getTestClass();
			if (testClass == null) return null;
			return Description.createSuiteDescription(testClass);
		}

		private static Class<?>[] directCategories(Description description) {
			if (description == null) return NO_CATEGORIES;
			final Category annotation= description.getAnnotation(Category.class);
			if (annotation == null) return NO_CATEGORIES;
			return annotation.value();
		}

		private static Set<Class<?>> copyAndRefine(final Class<?> clazz, final Set<Class<?>> fallback) {
			return clazz == null ? fallback : singleton(clazz);
		}

		private static Set<Class<?>> copyAndRefine(final Collection<Class<?>> classes, final Set<Class<?>> fallback) {
			if (classes == null || classes.isEmpty()) return fallback;
			final Set<Class<?>> c= new HashSet<Class<?>>(classes);
			return c.remove(null) && c.isEmpty() ? fallback : c;
		}
	}

	public Categories(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
		try {
			filter(new CategoryFilter(getIncludedCategory(klass), getIncludedSelection(klass),
                                        getExcludedCategory(klass), getExcludedSelection(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		} catch (ClassNotFoundException e) {
			throw new InitializationError(e);
		}
		assertNoCategorizedDescendentsOfUncategorizeableParents(getDescription());
	}

	private static Set<Class<?>> getIncludedCategory(Class<?> klass) throws ClassNotFoundException {
		IncludeCategory annotation= klass.getAnnotation(IncludeCategory.class);
		return intersectWithSystemPropertyInclusions(annotation == null ? null : createSet(annotation.value()));
	}

	private static Selection getIncludedSelection(Class<?> klass) {
		IncludeCategory annotation= klass.getAnnotation(IncludeCategory.class);
		return annotation == null || annotation.assignableTo() == null ? DEFAULT_IN_SELECT : annotation.assignableTo();
	}

	private static Set<Class<?>> getExcludedCategory(Class<?> klass) throws ClassNotFoundException {
		ExcludeCategory annotation= klass.getAnnotation(ExcludeCategory.class);
		return unionWithSystemPropertyExclusions(annotation == null ? createSet() : createSet(annotation.value()));
	}

	private static Selection getExcludedSelection(Class<?> klass) {
		ExcludeCategory annotation= klass.getAnnotation(ExcludeCategory.class);
		return annotation == null || annotation.assignableTo() == null ? DEFAULT_EX_SELECT : annotation.assignableTo();
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

	private static Set<Class<?>> intersectWithSystemPropertyInclusions(Set<Class<?>> includes) throws ClassNotFoundException {
		final Class<?>[] extCategories= getCategoriesBySystemProperty("org.junit.categories.included");
		if (includes == null || includes.remove(null) && includes.isEmpty()) {///ready for plural in IncludeCategory
			if (extCategories.length == 0) return null;//included categories are all
			if (includes == null) includes= new HashSet<Class<?>>(extCategories.length);
			Collections.addAll(includes, extCategories);
			return includes;//we have some categories from external system property 'org.junit.categories.included'
		} else if (extCategories.length == 0) return includes;
		final Set<Class<?>> subCategories= new HashSet<Class<?>>();
		for (final Class<?> extCategory : extCategories)
			if (hasAssignableFrom(includes, extCategory)) subCategories.add(extCategory);
		return subCategories;//if empty, nothing to test
	}

	private static Set<Class<?>> unionWithSystemPropertyExclusions(Set<Class<?>> excludes) throws ClassNotFoundException {
		excludes.remove(null);///ready for plural in ExcludeCategory
		Collections.addAll(excludes, getCategoriesBySystemProperty("org.junit.categories.excluded"));
		return excludes;//if empty union, nothing to exclude
	}

	private static Class<?>[] getCategoriesBySystemProperty(final String systemPropertyKey) throws ClassNotFoundException {
		final List<Class<?>> categories= new ArrayList<Class<?>>();
		for (String clazz : System.getProperty(systemPropertyKey, "").split(",")) {
			clazz= clazz.trim();
			if (clazz.length() != 0) {
				if (clazz.length() <= ".java".length()
                                    || !clazz.regionMatches(true, clazz.length() - ".java".length(), ".java", 0, ".java".length()))
                                        throw new ClassNotFoundException("file name must be finished by \".java\" ignoring case");
				clazz= clazz.substring(0, clazz.length() - ".java".length());
				if (clazz.indexOf('.') != -1)
                                    throw new ClassNotFoundException("File name must not contain '.'. Instead use slash '/'.");
				clazz= clazz.replace('/', '.');
				categories.add(Class.forName(clazz));
			}
		}
		return categories.toArray(new Class[categories.size()]);
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

    private static boolean hasAssignableTo(Set<Class<?>> assigns, Class<?> to) {
        for (final Class<?> from : assigns)
            if (to.isAssignableFrom(from)) return true;
        return false;
    }

    private static Set<Class<?>> createSet(Class<?>... t) {
        final Set<Class<?>> set= new HashSet<Class<?>>();
        Collections.addAll(set, t);
        return set;
    }

    private static Set<Class<?>> singleton(Class<?> o) {
        final Set<Class<?>> set= new HashSet<Class<?>>();
        set.add(o);
        return set;
    }
}