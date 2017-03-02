package org.junit.rules;

import static java.util.Calendar.LONG;
import static java.util.Calendar.MONTH;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class SetLocaleExampleTest {
	@Rule
	public SetLocale setLocale= new SetLocale(Locale.FRENCH);

	@Test
	public void testExample() {
		Calendar cal= GregorianCalendar.getInstance();
		cal.set(2000, 0, 1);

		Assert.assertEquals("Month does not match", "janvier",
				cal.getDisplayName(MONTH, LONG, Locale.getDefault()));
	}
}
