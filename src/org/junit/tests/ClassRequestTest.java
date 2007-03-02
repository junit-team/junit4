package org.junit.tests;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.internal.requests.ClassRequest;

public class ClassRequestTest {
	public static class PrivateSuiteMethod {
		static junit.framework.Test suite() {
			return null;
		}
	}
	
	@Test public void noSuiteMethodIfMethodPrivate() {
		assertFalse(new ClassRequest(PrivateSuiteMethod.class).hasSuiteMethod());
	}
}
