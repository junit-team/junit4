/**
 * 
 */
package org.junit.experimental.imposterization;

import org.jmock.api.Imposteriser;
import org.jmock.api.Invokable;
import org.jmock.lib.legacy.ClassImposteriser;

public class PopperImposterizer {
	private final Invokable invokable;

	public PopperImposterizer(Invokable invokable) {
		this.invokable = invokable;
	}

	@SuppressWarnings("unchecked") public <T> T imposterize(Class<T> type) {
		Imposteriser imposterizer = ClassImposteriser.INSTANCE;
		if (type.getName().contains("EnhancerByCGLIB")) {
			return (T) imposterizer.imposterise(invokable,
					type.getSuperclass(), type.getInterfaces());
		} else {
			return imposterizer.imposterise(invokable, type);
		}
	}
}