package junit.tests.framework;

import junit.framework.BlockedException;
import junit.framework.TestCase;

public class BlockedExceptionTest extends TestCase {
	private static final String ARBITRARY_MESSAGE= "arbitrary message";

	public void testCreateExceptionWithoutMessage() throws Exception {
		BlockedException exception= new BlockedException();
		assertNull(exception.getMessage());
	}

	public void testCreateExceptionWithMessage() throws Exception {
		BlockedException exception= new BlockedException(ARBITRARY_MESSAGE);
		assertEquals(ARBITRARY_MESSAGE, exception.getMessage());
	}

	public void testCreateExceptionWithoutMessageInsteadOfNull() throws Exception {
		BlockedException exception= new BlockedException(null);
		assertEquals("", exception.getMessage());
	}
}
