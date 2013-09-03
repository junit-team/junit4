package org.junit.rules;

/**
 * A Junit rule to be used to set system properties before tests,
 * and reset to original values when the test has completed
 *
 * <pre>
 * public class HasSystemPropertyTest {
 *
 *     &#064;Rule
 *     public SystemProperty systemProperty = new SystemProperty("name", "value");
 *
 *     &#064;Test
 *     public void propertyTest() {
 *          assertEquals("value", System.getProperty("name"));
 *     }
 * }
 * </pre>
 *
 * @author zapodot at gmail dot com
 */
public class SystemProperty extends ExternalResource {

    private final String propertyName;
    private final String propertyValue;
    private String originalPropertyValue = null;

    public SystemProperty(final String propertyName, final String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    @Override
    protected void before() throws Throwable {
        originalPropertyValue = System.setProperty(propertyName, propertyValue);

    }

    @Override
    protected void after() {
        if (originalPropertyValue == null) {
            System.clearProperty(propertyName);
        } else {
            System.setProperty(propertyName, originalPropertyValue);
        }
    }
}
