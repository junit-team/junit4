package org.junit.tests.assertion;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ComparisonFailureTest {
		
	private String expected, actual, message;
	
	public ComparisonFailureTest(String e, String a, String m) {
		expected = e;
		actual = a;
		message = m;
	}
	
	@Parameters(name = "compact-msg-{index}, exp=\"{1}\"")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			// simple base case
			{ "a", "b", "expected:<[a]> but was:<[b]>" },
				
			// common prefix
			{ "ba", "bc", "expected:<b[a]> but was:<b[c]>" },
				
			// common suffix
			{ "ab", "cb", "expected:<[a]b> but was:<[c]b>" },
				
			// common pre and suffix
			{ "abc", "adc", "expected:<a[b]c> but was:<a[d]c>" },
			
			// expected is subset of actual
			{ "ab", "abc", "expected:<ab[]> but was:<ab[c]>" },

			// expected is superset of actual
			{ "abc", "ab", "expected:<ab[c]> but was:<ab[]>" },
			
			// overlapping matches.
			{ "abc", "abbc", "expected:<ab[]c> but was:<ab[b]c>" },

			// long prefix yielding "..."
			{ "01234567890123456789PRE:hello:POST", 
				"01234567890123456789PRE:world:POST",
				"expected:<...4567890123456789PRE:[hello]:POST> but was:<...4567890123456789PRE:[world]:POST>" },
					
			// long suffix	yielding "..."
			{ "PRE:hello:01234567890123456789POST",
				"PRE:world:01234567890123456789POST",
				"expected:<PRE:[hello]:0123456789012345678...> but was:<PRE:[world]:0123456789012345678...>"	
			},
					
			// bug609972
			{ "S&P500", "0", "expected:<[S&P50]0> but was:<[]0>" },
			
			// empty expected string
			{ "", "a", "expected:<[]> but was:<[a]>" },

			// empty actual string
			{ "a", "", "expected:<[a]> but was:<[]>" }

		});	
	}

	@Test
	public void compactFailureMessage() {
		ComparisonFailure failure = new ComparisonFailure("", expected, actual);
		assertEquals(message, failure.getMessage());
	}
	
}
