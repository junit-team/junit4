/**
 * 
 */
package org.junit.experimental.imposterization;

import static org.hamcrest.CoreMatchers.nullValue;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.junit.Assume.AssumptionViolatedException;

public class AssumePassing {
	/**
	 * This will stay here forever, but dependencies do not add long-term value.
	 * The compile error will remind you to remove references when done with
	 * dependency
	 */

	@Deprecated public static <T> T assumePasses(final Class<T> type) {
		return new PopperImposterizer(new Invokable() {
			public Object invoke(Invocation invocation) throws Throwable {
				try {
					invocation.applyTo(type.newInstance());
				} catch (AssumptionViolatedException e) {
					return null;
				} catch (Throwable thrown) {
					throw new AssumptionViolatedException(thrown, nullValue());
				}
				return null;
			}
		}).imposterize(type);
	}
}