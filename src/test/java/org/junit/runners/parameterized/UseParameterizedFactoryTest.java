package org.junit.runners.parameterized;

import org.junit.Assert;
import org.junit.Test;

public class UseParameterizedFactoryTest extends
        UseParameterizedFactoryAbstractTest {

    public UseParameterizedFactoryTest(String parameter) {

    }

    @Test
    public void parameterizedTest() {
        Assert.assertTrue(testFlag);
    }
}
