package org.junit.filters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author nyap@netflix.com (Noel Yap)
 */
public class ClassUtilTest {
    @Test
    public void convertFqnToClassPathShouldWorkCorrectly() {
        final String expected = "com/netflix/package/Class.class";

        final String actual = ClassUtil.convertFqnToClassPath("com.netflix.package.Class");

        assertEquals(actual, expected);
    }
}
