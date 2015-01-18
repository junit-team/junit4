package org.junit.rules;

import org.junit.runner.Description;

/**
 * The TestName Rule makes the current test name available inside test methods:
 *
 * <pre>
 * public class TestNameTest {
 *  &#064;Rule
 *  public TestName name= new TestName();
 *
 *  &#064;Test
 *  public void testA() {
 *      assertEquals(&quot;testA&quot;, name.getMethodName());
 *     }
 *
 *  &#064;Test
 *  public void testB() {
 *      assertEquals(&quot;testB&quot;, name.getMethodName());
 *     }
 * }
 * </pre>
 *
 * @since 4.7
 */
public class TestName extends TestWatcher {
    private String name;

    @Override
    protected void starting(Description d) {
        name = d.getMethodName();
    }

    /**
     * @return the name of the currently-running test method
     */
    public String getMethodName() {
        return name;
    }
}
