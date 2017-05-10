package org.junit.experimental.annotations;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Annotations extends BlockJUnit4ClassRunner {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Include {

        public Class<? extends Annotation>[] value() default {};

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface Exclude {

        public Class<? extends Annotation>[] value() default {};

    }

    private class AnnotationFilter extends Filter {

        private final Set<Class<? extends Annotation>> included;

        private final Set<Class<? extends Annotation>> excluded;

        private final Set<Class<? extends Annotation>> ignored;

        private AnnotationFilter(Set<Class<? extends Annotation>> included, Set<Class<? extends Annotation>> excluded, Set<Class<? extends Annotation>> ignored) {
            this.included = included;
            this.excluded = excluded;
            this.ignored = ignored;
        }

        @Override
        public boolean shouldRun(Description description) {
            Set<Annotation> annotations = getFilteredAnnotations(description);

            // No annotation and no include clause, run the test.

            if (annotations.isEmpty()) {
                return included.isEmpty();
            }

            // At least one annotation matches an exclude clause, don't run the test.

            if (!excluded.isEmpty()) {
                for (Annotation annotation : annotations) {
                    if (excluded.contains(annotation.annotationType())) {
                        return false;
                    }
                }
            }

            // Run the test only if every include clause is satisfied.

            if (!included.isEmpty()) {
                for (Annotation annotation : annotations) {
                    if (!included.contains(annotation.annotationType())) {
                        return false;
                    }
                }
            }

            return true;
        }

        private Set<Annotation> getFilteredAnnotations(Description description) {
            final Set<Annotation> filtered = new HashSet<Annotation>();

            for (Annotation annotation : description.getAnnotations()) {
                if (ignored.contains(annotation.annotationType())) {
                    continue;
                }

                filtered.add(annotation);
            }

            return filtered;
        }

        @Override
        public String describe() {
            return toString();
        }

        @Override
        public String toString() {
            String result = "AnnotationFilter(";

            result += "includes=";
            result += included.isEmpty() ? "everything" : annotationClassesToString(included);

            result += ",";

            result += "excludes=";
            result += excluded.isEmpty() ? "nothing" : annotationClassesToString(excluded);

            result += ")";

            return result;
        }

        private String annotationClassesToString(Set<Class<? extends Annotation>> classes) {
            String result = "[";

            final Class[] array = classes.toArray(new Class[classes.size()]);

            for (int i = 0; i < array.length - 1; i++) {
                result += array[i] + ",";
            }

            result += array[array.length - 1];

            result += "]";

            return result;
        }

    }

    public Annotations(Class<?> klass) throws InitializationError {
        super(klass);

        try {
            filter(new AnnotationFilter(getIncludedAnnotations(), getExcludedAnnotations(), getIgnoredAnnotations()));
        } catch (NoTestsRemainException e) {
            throw new InitializationError(e);
        }
    }

    private Set<Class<? extends Annotation>> getIncludedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

        final Include include = getDescription().getAnnotation(Include.class);

        if (include != null) {
            Collections.addAll(annotations, include.value());
        }

        return annotations;
    }

    private Set<Class<? extends Annotation>> getExcludedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

        final Exclude include = getDescription().getAnnotation(Exclude.class);

        if (include != null) {
            Collections.addAll(annotations, include.value());
        }

        return annotations;
    }

    protected Set<Class<? extends Annotation>> getIgnoredAnnotations() {
        final Set<Class<? extends Annotation>> ignored = new HashSet<Class<? extends Annotation>>();

        ignored.add(Test.class);
        ignored.add(Category.class);

        return ignored;
    }

}
