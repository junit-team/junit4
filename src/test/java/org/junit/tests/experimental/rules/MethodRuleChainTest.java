package org.junit.tests.experimental.rules;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.rules.MethodRuleChain.outerRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.MethodRuleChain;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class MethodRuleChainTest {

	 private static final List<String> LOG = new ArrayList<String>();

	    private static class LoggingMethodRule implements MethodRule {
	        private final String label;

	        public LoggingMethodRule(String label) {
	            this.label = label;
	        }

			public Statement apply(final Statement base, final FrameworkMethod method,
					final Object target) {
				return new Statement() {
					
					@Override
					public void evaluate() throws Throwable {
						LOG.add("starting "+ label);
						base.evaluate();
						LOG.add("finished "+ label);
					}
				};
			}

	       
	    }

	    public static class UseRuleChain {
	        @Rule
	        public final MethodRuleChain chain = outerRule(new LoggingMethodRule("outer rule"))
	                .around(new LoggingMethodRule("middle rule")).around(
	                        new LoggingMethodRule("inner rule"));

	        @Test
	        public void example() {
	            assertTrue(true);
	        }
	    }

	    @Test
	    public void executeRulesInCorrectOrder() throws Exception {
	        testResult(UseRuleChain.class);
	        List<String> expectedLog = asList("starting outer rule",
	                "starting middle rule", "starting inner rule",
	                "finished inner rule", "finished middle rule",
	                "finished outer rule");
	        assertEquals(expectedLog, LOG);
	    }
	
	
}
