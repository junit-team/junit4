package org.junit.experimental.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * From a given set of test classes, runs only the classes and methods that are
 * annotated with either the category given with the @IncludeCategory
 * annotation, or a subtype of that category.
 *
 * Note that, for now, annotating suites with {@code @Category} has no effect.
 * Categories must be annotated on the direct method or class.
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
 *  &#064;Test
 *  public void a() {
 *      fail();
 *     }
 *
 *  &#064;Category(SlowTests.class)
 *  &#064;Test
 *  public void b() {
 *     }
 * }
 *
 * &#064;Category( { SlowTests.class, FastTests.class })
 * public static class B {
 *  &#064;Test
 *  public void c() {
 *
 *     }
 * }
 *
 * &#064;RunWith(Categories.class)
 * &#064;IncludeCategory(SlowTests.class)
 * &#064;SuiteClasses( { A.class, B.class })
 * // Note that Categories is a kind of Suite
 * public static class SlowTestSuite {
 * }
 * </pre>
 */
public class Categories extends Suite {
    // the way filters are implemented makes this unnecessarily complicated,
    // buggy, and difficult to specify.  A new way of handling filters could
    // someday enable a better new implementation.
    // https://github.com/KentBeck/junit/issues/issue/172

    /**
     * {@link IncludeCategory} determines which Tests are ran by {@link Categories}.
     * <p/>
     *  If specified all Tests annotated with classes specified in the value of this annotation will be ran by
     * {@link Categories} as long they are not specified using {@link ExcludeCategory}
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IncludeCategory {
        public Class<?>[] value() default {};
    }

    /**
     * {@link ExcludeCategory} determines which Tests are skipped by {@link Categories}.
     * <p/>
     * If specified all Tests annotated with classes specified in the value of this annotation will be skipped by
     * {@link Categories} regardless of being mentioned in {@link IncludeCategory}
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExcludeCategory {
        public Class<?>[] value() default {};
    }

    public static class CategoryFilter extends Filter {

        public static CategoryFilter include(Class<?> categoryType) {
            return new CategoryFilter(new Class<?>[]{categoryType}, null);
        }

        public static CategoryFilter include(Class<?>... categoryType) {
            return new CategoryFilter(categoryType, null);
        }

        private Class<?>[] fIncludedCategories = new Class<?>[]{};

        private Class<?>[] fExcludedCategories = new Class<?>[]{};

        public CategoryFilter(Class<?> includedCategory, Class<?> excludedCategory) {
            if (includedCategory != null){
                fIncludedCategories = new Class<?>[]{includedCategory};
            }
            if (excludedCategory != null){
                fExcludedCategories = new Class<?>[]{excludedCategory};
            }
        }

        public CategoryFilter(Class<?>[] includedCategories,
                Class<?>[] excludedCategories) {
            if (includedCategories != null){
                fIncludedCategories = includedCategories;
            }
            if (excludedCategories != null){
                fExcludedCategories = excludedCategories;
            }
        }

        @Override
        public String describe() {
            StringBuilder description = new StringBuilder("Categories: ");
            if (fIncludedCategories.length > 0){
                description.append("Included categories: ");
                for (Class<?> each : fIncludedCategories){
                    description.append(each.getSimpleName()).append(" ");
                }
            } else{
                description.append("No included categories ");
            }
            description.append("| ");
            if (fExcludedCategories.length > 0){
                description.append("Excluded categories: ");
                for (Class<?> each : fExcludedCategories){
                    description.append(each.getSimpleName()).append(" ");
                }
            } else{
                description.append("No excluded categories");
            }
            return description.toString();
        }

        @Override
        public boolean shouldRun(Description description) {
            if (hasCorrectCategoryAnnotation(description)) {
                return true;
            }
            for (Description each : description.getChildren()) {
                if (shouldRun(each)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks if the given array of classes (eg fIncludedCategories) contains a given class
         * This is used to determine if a specified class in a {@link Category} annotation is represented in the
         * class-array specified in a {@link IncludeCategory} or {@link ExcludeCategory} annotation of a suite ran with
         * {@link Categories}
         *
         * @param categories array of categories (specified value of {@link IncludeCategory} or {@link ExcludeCategory})
         * @param category defined category (one of the classes specified in {@link Category})
         * @return true if the given category class is assignable from any of the given classes in categories
         */
        private boolean containsCategory(Class<?>[] categories, Class<?> category){
            for (Class<?> each : categories){
                if (each.isAssignableFrom(category)){
                    return true;
                }
            }
            return false;
        }

        private boolean hasCorrectCategoryAnnotation(Description description) {
            List<Class<?>> categories = categories(description);
            if (categories.isEmpty()) {
                return fIncludedCategories.length == 0;
            }
            for (Class<?> each : categories) {
                if (containsCategory(fExcludedCategories, each)) {
                    return false;
                }
            }
            for (Class<?> each : categories) {
                if (fIncludedCategories.length == 0 || containsCategory(fIncludedCategories, each)) {
                    return true;
                }
            }
            return false;
        }

        private List<Class<?>> categories(Description description) {
            ArrayList<Class<?>> categories = new ArrayList<Class<?>>();
            categories.addAll(Arrays.asList(directCategories(description)));
            categories.addAll(Arrays.asList(directCategories(parentDescription(description))));
            return categories;
        }

        private Description parentDescription(Description description) {
            Class<?> testClass = description.getTestClass();
            if (testClass == null) {
                return null;
            }
            return Description.createSuiteDescription(testClass);
        }

        private Class<?>[] directCategories(Description description) {
            if (description == null) {
                return new Class<?>[0];
            }
            Category annotation = description.getAnnotation(Category.class);
            if (annotation == null) {
                return new Class<?>[0];
            }
            return annotation.value();
        }
    }

    public Categories(Class<?> klass, RunnerBuilder builder)
            throws InitializationError {
        super(klass, builder);
        try {
            filter(new CategoryFilter(getIncludedCategories(klass),
                    getExcludedCategories(klass)));
        } catch (NoTestsRemainException e) {
            throw new InitializationError(e);
        }
        assertNoCategorizedDescendentsOfUncategorizeableParents(getDescription());
    }

    private Class<?>[] getIncludedCategories(Class<?> klass) {
        IncludeCategory annotation = klass.getAnnotation(IncludeCategory.class);
        return annotation == null ? null : annotation.value();
    }

    private Class<?>[] getExcludedCategories(Class<?> klass) {
        ExcludeCategory annotation = klass.getAnnotation(ExcludeCategory.class);
        return annotation == null ? null : annotation.value();
    }

    private void assertNoCategorizedDescendentsOfUncategorizeableParents(Description description) throws InitializationError {
        if (!canHaveCategorizedChildren(description)) {
            assertNoDescendantsHaveCategoryAnnotations(description);
        }
        for (Description each : description.getChildren()) {
            assertNoCategorizedDescendentsOfUncategorizeableParents(each);
        }
    }

    private void assertNoDescendantsHaveCategoryAnnotations(Description description) throws InitializationError {
        for (Description each : description.getChildren()) {
            if (each.getAnnotation(Category.class) != null) {
                throw new InitializationError("Category annotations on Parameterized classes are not supported on individual methods.");
            }
            assertNoDescendantsHaveCategoryAnnotations(each);
        }
    }

    // If children have names like [0], our current magical category code can't determine their
    // parentage.
    private static boolean canHaveCategorizedChildren(Description description) {
        for (Description each : description.getChildren()) {
            if (each.getTestClass() == null) {
                return false;
            }
        }
        return true;
    }
}