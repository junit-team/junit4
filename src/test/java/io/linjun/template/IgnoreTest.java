package io.linjun.template;

import org.junit.Ignore;
import org.junit.Test;

public class IgnoreTest {
    @Ignore("Test is ignored as a demonstration")
    @Test
    public void testSame() throws Exception {
        throw new Exception("Test should be ignored!!");
    }
}
