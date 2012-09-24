package org.junit.rules;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SetLocaleTest {
	private TestRule rule;
	
	@Before
	public void setUp() {
		Locale.setDefault(Locale.ENGLISH);
		rule = new SetLocale(Locale.FRENCH);
	}
	
	@Test
	public void testDefaultRestored() throws Throwable {
		rule.apply(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// Empty
			}
		}, Description.TEST_MECHANISM).evaluate();
		Assert.assertEquals("Locale was not restored to default", Locale.ENGLISH, Locale.getDefault());
	}
	
	@Test
	public void testLocaleSet() throws Throwable {
		rule.apply(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				Assert.assertEquals("Locale was set", Locale.FRENCH, Locale.getDefault());
			}
		}, Description.TEST_MECHANISM).evaluate();
	}
}
