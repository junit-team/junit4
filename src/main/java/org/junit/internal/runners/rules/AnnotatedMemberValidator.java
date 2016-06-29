package org.junit.internal.runners.rules;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.TestClass;

/**
 * Base class for classe that validate fields and methods that are annotated with annotations
 */
abstract class AnnotatedMemberValidator {
    private final Class<? extends Annotation> annotation;
    private final boolean forMethods;
    private final List<MemberValidator> validators;

    AnnotatedMemberValidator(Builder<?> builder) {
        if (builder.annotation == null) {
            throw new IllegalStateException("Must call withAnnotation() before build()");
        }
        annotation = builder.annotation;
        forMethods = builder.forMethods;
        validators = new ArrayList<MemberValidator>(
                builder.validators.size() + (builder.isClassAnnotation ? 3 : 2));
        this.validators.add(MemberMustBePublic.INSTANCE);
        this.validators.add(DeclaringClassMustBePublic.INSTANCE);
        if (builder.isClassAnnotation) {
            this.validators.add(MemberMustBeStatic.INSTANCE);
        }
        validators.addAll(builder.validators);
    }

    /**
     * Validate the {@link org.junit.runners.model.TestClass} and adds reasons
     * for rejecting the class to a list of errors.
     *
     * @param target the {@code TestClass} to validate.
     * @param errors the list of errors.
     */
    public final void validate(TestClass target, List<Throwable> errors) {
        List<? extends FrameworkMember<?>> members = forMethods ? target.getAnnotatedMethods(annotation)
                : target.getAnnotatedFields(annotation);

        for (FrameworkMember<?> each : members) {
            validateMember(each, errors);
        }
    }

    private void validateMember(FrameworkMember<?> member, List<Throwable> errors) {
        for (MemberValidator strategy : validators) {
            strategy.validate(member, annotation, errors);
        }
    }

    protected static class Builder<B extends Builder<B>> {
        private Class<? extends Annotation> annotation;
        private boolean forMethods = false;
        private boolean isClassAnnotation = false;
        private final List<MemberValidator> validators = new ArrayList<MemberValidator>();

        protected Builder() {
        }
        
        B forClassAnnotation(Class<? extends Annotation> annotation) {
            this.annotation = checkNotNull(annotation);
            this.isClassAnnotation = true;
            return selfRef();
        }

        B forTestAnnotation(Class<? extends Annotation> annotation) {
            this.annotation = checkNotNull(annotation);
            this.isClassAnnotation = false;
            return selfRef();
        }

        B forMethods() {
            forMethods = true;
            return selfRef();
        }

        B withValidator(MemberValidator validator) {
            validators.add(checkNotNull(validator));
            return selfRef();
        }

        @SuppressWarnings("unchecked")
        private B selfRef() {
            return (B) this;
        }

        private <T> T checkNotNull(T t) {
            if (t == null) {
                throw new NullPointerException();
            }
            return t;
        }
    }

    /**
     * Requires the member's declaring class to be public
     */
    private static final class DeclaringClassMustBePublic implements MemberValidator {
        public static final DeclaringClassMustBePublic INSTANCE = new DeclaringClassMustBePublic();

        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!isDeclaringClassPublic(member)) {
                errors.add(new ValidationError(member, annotation,
                        "must be declared in a public class."));
            }
        }

        private boolean isDeclaringClassPublic(FrameworkMember<?> member) {
            return Modifier.isPublic(member.getDeclaringClass().getModifiers());
        }
    }

    /**
     * Requires the member to be static
     */
    private static final class MemberMustBeStatic implements MemberValidator {
        public static final MemberMustBeStatic INSTANCE = new MemberMustBeStatic();

        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!member.isStatic()) {
                errors.add(new ValidationError(member, annotation,
                        "must be static."));
            }
        }
    }
 
    /**
     * Requires the member to be public
     */
    private static final class MemberMustBePublic implements MemberValidator {
        public static final MemberMustBePublic INSTANCE = new MemberMustBePublic();

        public void validate(FrameworkMember<?> member, Class<? extends Annotation> annotation, List<Throwable> errors) {
            if (!member.isPublic()) {
                errors.add(new ValidationError(member, annotation,
                        "must be public."));
            }
        }
    }
}
