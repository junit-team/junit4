package org.junit.runners.model;

import java.lang.annotation.Annotation;

public interface FrameworkMember<T extends FrameworkMember<T>> {
	/**
	 * Returns the annotations on this method
	 */
	public Annotation[] getAnnotations();

	public boolean isShadowedBy(T otherMember);
}
