package org.junit.rules;

import java.util.Locale;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The SetLocale rule allows setting of a Locale for the duration
 * of a test.
 * 
 * <pre>
 * public static class UsesSetLocale {
 *   &#064;Rule
 *   SetLocale setLocale = new SetLocale(Locale.FRANCE);
 *   
 *   &#064;Test
 *   public void testExample() {
 *     Calendar cal= GregorianCalendar.getInstance();
 *     cal.set(2000, 0, 1);
 *
 *     Assert.assertEquals("Month does not match", "janvier",
 *       cal.getDisplayName(MONTH, LONG, Locale.getDefault()));
 *   }
 * }
 * </pre>
 */
public class SetLocale implements TestRule {
	private final Locale locale;
	
	/**
	 * Creates a new instance of {@code SetLocale} with the provided 
	 * {@link Locale}.
	 * 
	 * @param locale the locale to set.
	 */
	public SetLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement apply(Statement base, Description description) {
		return statement(base);
	}
	
	private Statement statement(final Statement base) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Locale orig = Locale.getDefault();
				Locale.setDefault(locale);
				try {
					base.evaluate();
				} finally {
					Locale.setDefault(orig);
				}
			}
		};
	}

}
