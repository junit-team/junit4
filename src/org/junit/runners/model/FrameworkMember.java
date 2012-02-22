package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.util.List;

abstract class FrameworkMember<T extends FrameworkMember<T>> {
	/**
	 * Returns the annotations on this method
	 */
	abstract Annotation[] getAnnotations();

	abstract boolean isShadowedBy(T otherMember);

	boolean isShadowedBy(List<T> members) {
		for (T each : members)
			if (isShadowedBy(each))
				return true;
		return false;
	}
}
