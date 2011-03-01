package org.junit.tests.experimental.theories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ParameterSignatureWithGenericsTest {
    @DataPoint
    public static Method getType() throws Exception {
        return ParameterSignatureWithGenericsTest.class.getMethod("foo", List.class, Class.class);
    }

    @DataPoint
    public static int ZERO= 0;

    @DataPoint
    public static int ONE= 1;

    public static void foo(List<String> items, Class<?> clazz) {
    }

    @Theory
    public void getType(Method method, int index) {
        assumeTrue(index < method.getParameterTypes().length);
        assertEquals(method.getGenericParameterTypes()[index], ParameterSignature
                .signatures(method).get(index).getType());
    }
}
