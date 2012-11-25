package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class AssertionFailedErrorTest extends TestCase {
    private static final String ARBITRARY_MESSAGE = "arbitrary message";

    public void testCreateErrorWithoutMessage() throws Exception {
        AssertionFailedError error = new AssertionFailedError();
        assertNull(error.getMessage());
    }

    public void testCreateErrorWithMessage() throws Exception {
        AssertionFailedError error = new AssertionFailedError(ARBITRARY_MESSAGE);
        assertEquals(ARBITRARY_MESSAGE, error.getMessage());
    }

    public void testCreateErrorWithoutMessageInsteadOfNull() throws Exception {
        AssertionFailedError error = new AssertionFailedError(null);
        assertEquals("", error.getMessage());
    }
}
