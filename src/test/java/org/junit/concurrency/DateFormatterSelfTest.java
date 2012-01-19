package org.junit.concurrency;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ConcurrencyJunitRunner.class)
@Concurrency(times = 1000)
public class DateFormatterSelfTest {
	private static final String FORMAT1 = "HH:mm:ss dd.MM.yyyy ZZZZ";
	
	private static final String VALUE1 = "23:59:00 31.12.1999 +0100";
	
	private static SimpleDateFormat FORMATTER1;
	
	private static Date DATE1;
	
	@BeforeClass
	public static void initDate() throws ParseException {
		FORMATTER1 = new SimpleDateFormat(FORMAT1);
		DATE1 = FORMATTER1.parse(VALUE1);
	}

	
	@Test
	public void testCorrectUsage() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT1);
		Date date1 = sdf.parse(VALUE1);
		Assert.assertEquals(DATE1, date1);
	}
	
	@Test
	@Concurrency(expectAtLeast = { AssertionError.class, RuntimeException.class })
	public void testIncorrectReuseInMultipleThreads() throws ParseException {
		Date date1 = FORMATTER1.parse(VALUE1);
		Assert.assertEquals(DATE1, date1);
	}
}
