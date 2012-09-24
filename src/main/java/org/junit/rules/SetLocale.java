package org.junit.rules;

import java.util.Locale;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SetLocale implements TestRule {
	private final Locale locale;
	
	public SetLocale(Locale locale) {
		this.locale = locale;
	}

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
