package junit.tests.framework;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class ComparisonFailureTest extends TestCase {

	public void testComparisonErrorMessage() {
		ComparisonFailure failure= new ComparisonFailure("a", "b", "c");
		assertEquals("a expected:<b> but was:<c>", failure.getMessage());
	}

	public void testComparisonErrorStartSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ba", "bc");
		assertEquals("expected:<...a> but was:<...c>", failure.getMessage());
	}

	public void testComparisonErrorEndSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "cb");
		assertEquals("expected:<a...> but was:<c...>", failure.getMessage());
	}

	public void testComparisonErrorSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "ab");
		assertEquals("expected:<ab> but was:<ab>", failure.getMessage());
	}

	public void testComparisonErrorStartAndEndSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "abc", "adc");
		assertEquals("expected:<...b...> but was:<...d...>", failure.getMessage());
	}

	public void testComparisonErrorStartSameComplete() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "abc");
		assertEquals("expected:<...> but was:<...c>", failure.getMessage());
	}

	public void testComparisonErrorEndSameComplete() {
		ComparisonFailure failure= new ComparisonFailure(null, "bc", "abc");
		assertEquals("expected:<...> but was:<a...>", failure.getMessage());
	}

	public void testComparisonErrorOverlapingMatches() {
		ComparisonFailure failure= new ComparisonFailure(null, "abc", "abbc");
		assertEquals("expected:<......> but was:<...b...>", failure.getMessage());
	}

	public void testComparisonErrorOverlapingMatches2() {
		ComparisonFailure failure= new ComparisonFailure(null, "abcdde", "abcde");
		assertEquals("expected:<...d...> but was:<......>", failure.getMessage());
	}

	public void testComparisonErrorWithActualNull() {
		ComparisonFailure failure= new ComparisonFailure(null, "a", null);
		assertEquals("expected:<a> but was:<null>", failure.getMessage());
	}
	
	public void testComparisonErrorWithExpectedNull() {
		ComparisonFailure failure= new ComparisonFailure(null, null, "a");
		assertEquals("expected:<null> but was:<a>", failure.getMessage());
	}
}
