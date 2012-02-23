package org.mearvk;

import static junit.framework.Assert.assertTrue;

@ClassRunOrder(order=2)
public class MyTestClass2
{
	@MethodRunOrder(order=1)
	public void runMe1()
	{
		assertTrue("Oops...runMe1", false);
	}
	
	@MethodRunOrder(order=2)
	public void runMe2()
	{
		assertTrue("Oops...runMe2", false);
	}
}
