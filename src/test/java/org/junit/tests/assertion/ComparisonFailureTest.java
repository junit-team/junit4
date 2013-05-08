package org.junit.tests.assertion;

import static org.junit.Assert.assertEquals;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class ComparisonFailureTest {
	
	@Test
	public void stringsWithoutPrePostFix() {
		ComparisonFailure cf = new ComparisonFailure("usermsg", "a", "b");
		assertEquals("usermsg expected:<[a]> but was:<[b]>", cf.getMessage());
	}

	@Test
    public void testStartSame() {
        ComparisonFailure failure = new ComparisonFailure("", "ba", "bc");
        assertEquals("expected:<b[a]> but was:<b[c]>", failure.getMessage());
    }

	@Test
    public void testEndSame() {
        ComparisonFailure failure = new ComparisonFailure("", "ab", "cb");
        assertEquals("expected:<[a]b> but was:<[c]b>", failure.getMessage());
    }

	
}
