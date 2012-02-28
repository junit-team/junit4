package org.mearvk;

import static junit.framework.Assert.assertTrue;

/**
 * A simple test class used to ensure that nothing major broke in the integration process
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
 * 
 *@author Max Rupplin
 */
@org.mearvk.ClassRunOrder(order = 2)
public class MyTestClass2
{
	@org.mearvk.MethodRunOrder(order = 1)
	public void runMe1()
	{
		assertTrue("Oops...runMe1", false);
	}

	@org.mearvk.MethodRunOrder(order = 2)
	public void runMe2()
	{
		assertTrue("Oops...runMe2", false);
	}
}
