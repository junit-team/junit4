package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

// TODO: better name
// TODO: convince ourselves interface is right
public interface BisectionRule {
	// TODO: javadoc
	Statement apply(Statement base, Description description);
}
