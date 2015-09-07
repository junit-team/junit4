package org.junit.internal.runners.rules;

import org.junit.runners.model.FrameworkMember;

import java.lang.annotation.Annotation;

class ValidationError extends Exception {

    private static final long serialVersionUID = 3176511008672645574L;

    public ValidationError(FrameworkMember<?> member, Class<? extends Annotation> annotation, String suffix) {
        super(String.format("The @%s '%s' %s", annotation.getSimpleName(), member.getName(), suffix));
    }
}
