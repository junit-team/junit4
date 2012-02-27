package org.mearvk;

import static junit.framework.Assert.assertTrue;

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
