package org.junit.tests.experimental.theories;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ParameterSignatureTest {
    @DataPoint
    public static Method getType() throws SecurityException,
            NoSuchMethodException {
        return ParameterSignatureTest.class.getMethod("getType", Method.class,
                int.class);
    }

    @DataPoint
    public static int ZERO = 0;

    @DataPoint
    public static int ONE = 1;

    @Theory
    public void getType(Method method, int index) {
        assumeTrue(index < method.getParameterTypes().length);
        assertEquals(method.getParameterTypes()[index], ParameterSignature
                .signatures(method).get(index).getType());
    }

    public void foo(@TestedOn(ints = {1, 2, 3})
    int x) {
    }

    @Test
    public void getAnnotations() throws SecurityException,
            NoSuchMethodException {
        Method method = ParameterSignatureTest.class.getMethod("foo", int.class);
        List<Annotation> annotations = ParameterSignature.signatures(method)
                .get(0).getAnnotations();
        assertThat(annotations,
                CoreMatchers.<TestedOn>hasItem(isA(TestedOn.class)));
    }
}
