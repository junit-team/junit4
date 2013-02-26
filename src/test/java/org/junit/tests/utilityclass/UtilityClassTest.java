package org.junit.tests.utilityclass;

import org.junit.Assert;
import static org.junit.Assert.utilityClassWellDefined;
import org.junit.Test;

/**
 * Tests {@link Assert#utilityClassWellDefined}.
 * 
 * @author Archimedes Trajano
 * 
 */
public class UtilityClassTest {

	@Test(expected= AssertionError.class)
	public void testBadUtil1() throws Exception {
		utilityClassWellDefined(NonFinalUtil.class);
	}

	@Test(expected= AssertionError.class)
	public void testBadUtil2() throws Exception {
		utilityClassWellDefined(PublicConstructorUtil.class);
	}

	@Test(expected= AssertionError.class)
	public void testBadUtil3() throws Exception {
		utilityClassWellDefined(MultipleConstructorUtil.class);
	}

	@Test
	public void testProperUtil() throws Exception {
		utilityClassWellDefined(ProperUtil.class);
	}

	@Test(expected= AssertionError.class)
	public void testTest() throws Exception {
		utilityClassWellDefined(UtilityClassTest.class);
	}
}