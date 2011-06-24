package org.junit.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils {

	public static <T extends Annotation> T findClassMetaAnnotation(Class<?> clazz, Class<T> annotationType) {
		// Look for annotation found directly
		T found = clazz.getAnnotation(annotationType);
		if (found != null) {
			return found;
		}

		// Search annotations (i.e. meta-annotation)
		for (Annotation metaAnnotation : clazz.getAnnotations()) { // FIXME: not picking up anything
			found = metaAnnotation.annotationType().getAnnotation(annotationType);
			if (found != null) {
				return found;
			}
		}
		
		// not found
		return null;
	}

	public static <T extends Annotation> T findMethodMetaAnnotation(Method method, Class<T> annotationType) {
		// Look for annotation found directly
		T found = method.getAnnotation(annotationType);
		if (found != null) {
			return found;
		}

		// Search annotations (i.e. meta-annotation)
		for (Annotation metaAnnotation : method.getAnnotations()) {
			found = metaAnnotation.annotationType().getAnnotation(annotationType);
			if (found != null) {
				return found;
			}
		}
		
		// not found
		return null;
	}
}
