package org.mearvk;

import static org.junit.Assert.assertTrue;

/**
 * A simple, non-ordered test class used to ensure that nothing major broke in the integration process
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
 * 
 * @author Max Rupplin
 */
public class MyTestClass3
{
	@org.junit.Test
	public void test1()
	{
		assertTrue("Oops...", true);
	}

	@org.junit.Test
	public void test2()
	{
		assertTrue("Oops...", false);
	}
}
