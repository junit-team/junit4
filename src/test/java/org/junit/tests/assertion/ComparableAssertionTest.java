package org.junit.tests.assertion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquivalent;
import static org.junit.Assert.assertGreaterThan;
import static org.junit.Assert.assertLessThan;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Tests for {@link java.lang.Comparable} assertions in {@link org.junit.Assert}
 * 
 * @author leet3lite.junit@soodonims.com (Alan Escreet)
 */
public class ComparableAssertionTest {

	/**
	 * Stub to act as a generic {@link Comparable} type in the comparison method
	 * tests.
	 */
	private static class ComparableStub implements Comparable<ComparableStub> {
		private final int compareToReturnValue;
		private final boolean equalsReturnValue;

		public ComparableStub(int compareToReturnValue,
				boolean equalsReturnValue) {
			this.compareToReturnValue= compareToReturnValue;
			this.equalsReturnValue= equalsReturnValue;
		}

		public int compareTo(ComparableStub o) {
			return this.compareToReturnValue;
		}
		
		@Override
		public boolean equals(Object o) {
			return this.equalsReturnValue;
		}
		
		@Override
		public String toString() {
			return String.valueOf(this.compareToReturnValue);
		}
	}
	
	private static final ComparableStub NULL_VALUE = null;
	private static final ComparableStub REFERENCE_VALUE = new ComparableStub(0, true);
	private static final ComparableStub EQUIVALENT_VALUE = new ComparableStub(0, false);
	private static final ComparableStub LOWER_VALUE = new ComparableStub(-1, false);
	private static final ComparableStub HIGHER_VALUE = new ComparableStub(1, false);
	
	@Test
	public void assertLessThanFailsWithSelfExplanatoryMessage() {
		try {
			assertLessThan(REFERENCE_VALUE, REFERENCE_VALUE);
		} catch (AssertionError e) {
			assertEquals(
					"Expected less than: " + ComparableStub.class.getName()
							+ '<' + REFERENCE_VALUE + "> but was: "
							+ ComparableStub.class.getName() + '<'
							+ REFERENCE_VALUE + '>', e.getMessage());
			return;
		}
		fail("Expected AssertionError");
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWhenBothValuesNull() {
		assertLessThan(NULL_VALUE, NULL_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWhenReferenceNull() {
		assertLessThan(null, REFERENCE_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWhenActualNull() {
		assertLessThan(REFERENCE_VALUE, null);
	}
	
	@Test
	public void assertLessThanShouldPassWhenActualLessThanReference() {
		assertLessThan(REFERENCE_VALUE, LOWER_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWhenActualEqualToReference() {
		assertLessThan(REFERENCE_VALUE, REFERENCE_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWhenActualGreaterThanReference() {
		assertLessThan(REFERENCE_VALUE, HIGHER_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWhenBothValuesNull() {
		assertGreaterThan(NULL_VALUE, NULL_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWhenReferenceNull() {
		assertGreaterThan(null, REFERENCE_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWhenActualNull() {
		assertGreaterThan(REFERENCE_VALUE, null);
	}
	
	@Test
	public void assertGreaterThanShouldPassWhenActualGreaterThanReference() {
		assertGreaterThan(REFERENCE_VALUE, HIGHER_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWhenActualEqualToReference() {
		assertGreaterThan(REFERENCE_VALUE, REFERENCE_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWhenActualLessThanReference() {
		assertGreaterThan(REFERENCE_VALUE, LOWER_VALUE);
	}
	
	@Test
	public void assertEquivalentShouldPassWhenActualEqualToReference() {
		assertEquivalent(REFERENCE_VALUE, REFERENCE_VALUE);
	}
	
	@Test
	public void assertEquivalentShouldPassWhenActualEquivalentToButNotEqualToReference() {
		assertEquivalent(REFERENCE_VALUE, EQUIVALENT_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentShouldFailWhenActualLessThanReference() {
		assertEquivalent(REFERENCE_VALUE, LOWER_VALUE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentShouldFailWhenActualGreaterThanReference() {
		assertEquivalent(REFERENCE_VALUE, HIGHER_VALUE);
	}
	
	@Test
	public void assertLessThanShouldPassWithAutoboxedInt() {
		assertLessThan(0, -1);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWithAutoboxedInt() {
		assertLessThan(0, 0);
	}
	
	@Test
	public void assertGreaterThanShouldPassWithAutoboxedInt() {
		assertGreaterThan(0, 1);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWithAutoboxedInt() {
		assertGreaterThan(0, 0);
	}
	
	@Test
	public void assertEquivalentShouldPassWithAutoboxedInt() {
		assertEquivalent(0, 0);
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentThanShouldFailWithAutoboxedInt() {
		assertEquivalent(0, 1);
	}
	
	@Test
	public void assertLessThanShouldPassWithAutoboxedDouble() {
		assertLessThan(0.0, -1.0);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWithAutoboxedDouble() {
		assertLessThan(0.0, 0.0);
	}
	
	@Test
	public void assertGreaterThanShouldPassWithAutoboxedDouble() {
		assertGreaterThan(0.0, 1.0);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWithAutoboxedDouble() {
		assertGreaterThan(0.0, 0.0);
	}
	
	@Test
	public void assertEquivalentShouldPassWithAutoboxedDouble() {
		assertEquivalent(0.0, 0.0);
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentThanShouldFailWithAutoboxedDouble() {
		assertEquivalent(0.0, 1.0);
	}
	
	@Test
	public void assertLessThanShouldPassWithString() {
		assertLessThan("b", "a");
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWithString() {
		assertLessThan("b", "b");
	}
	
	@Test
	public void assertGreaterThanShouldPassWithString() {
		assertGreaterThan("b", "c");
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWithString() {
		assertGreaterThan("b", "b");
	}
	
	@Test
	public void assertEquivalentShouldPassWithString() {
		assertEquivalent("b", "b");
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentThanShouldFailWithString() {
		assertEquivalent("b", "a");
	}
	
	@Test
	public void assertLessThanShouldPassWithAutoboxedChar() {
		assertLessThan('b', 'a');
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWithAutoboxedChar() {
		assertLessThan('b', 'b');
	}
	
	@Test
	public void assertGreaterThanShouldPassWithAutoboxedChar() {
		assertGreaterThan('b', 'c');
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWithAutoboxedChar() {
		assertGreaterThan('b', 'b');
	}
	
	@Test
	public void assertEquivalentShouldPassWithAutoboxedChar() {
		assertEquivalent('b', 'b');
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentThanShouldFailWithAutoboxedChar() {
		assertEquivalent('b', 'a');
	}
	
	@Test
	public void assertLessThanShouldPassWithBigDecimal() {
		assertLessThan(BigDecimal.ONE, BigDecimal.ZERO);
	}
	
	@Test(expected=AssertionError.class)
	public void assertLessThanShouldFailWithBigDecimal() {
		assertLessThan(BigDecimal.ZERO, BigDecimal.ZERO);
	}
	
	@Test
	public void assertGreaterThanShouldPassWithBigDecimal() {
		assertGreaterThan(BigDecimal.ZERO, BigDecimal.ONE);
	}
	
	@Test(expected=AssertionError.class)
	public void assertGreaterThanShouldFailWithBigDecimal() {
		assertGreaterThan(BigDecimal.ZERO, BigDecimal.ZERO);
	}
	
	@Test
	public void assertEquivalentShouldPassWithBigDecimal() {
		assertEquivalent(BigDecimal.ZERO, BigDecimal.ZERO);
	}
	
	@Test(expected=AssertionError.class)
	public void assertEquivalentThanShouldFailWithBigDecimal() {
		assertEquivalent(BigDecimal.ZERO, BigDecimal.ONE);
	}
}
