package org.mearvk;

import org.junit.ClassRunOrder;
import org.junit.MethodRunOrder;

@ClassRunOrder(order=2)
public class MyTestClass2
{
	@MethodRunOrder(order=1)
	public void runMe1()
	{
		//assertTrue("Oops...runMe1", false);
		System.err.println("runMe1 was run...");
	}
	
	@MethodRunOrder(order=2)
	public void runMe2()
	{
		//assertTrue("Oops...runMe2", false);
		System.err.println("runMe2 was run...");
	}
}
