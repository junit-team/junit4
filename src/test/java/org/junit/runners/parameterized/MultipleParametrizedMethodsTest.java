package org.junit.runners.parameterized;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MultipleParametrizedMethodsTest {
    @Parameter
    String string;

    @Parameters(name = "{method}:{index} {0}")
    public static Object[][] parameters1() {
        return new Object[][]{{"foo"}, {"bar"}, {"baz"}};
    }

    @Parameters(name = "{method}:{index} {0}")
    public static Iterable<Object[]> parameters2() {
        return new ArrayList<Object[]>() {{
            add(new Object[]{"1"});
            add(new Object[]{"2"});
            add(new Object[]{"42"});
        }};
    }

    @Test
    public void doNothing() {}
}