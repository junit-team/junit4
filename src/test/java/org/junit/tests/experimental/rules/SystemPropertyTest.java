package org.junit.tests.experimental.rules;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.SystemProperty;

import static org.junit.Assert.assertEquals;

/**
 * Testing the SystemProperty rule
 *
 * @author zapodot at gmail dot com
 */
public class SystemPropertyTest {

    private static class SystemPropertyWithPublicBeforeAndAfter extends SystemProperty {
        private SystemPropertyWithPublicBeforeAndAfter(final String propertyName, final String propertyValue) {
            super(propertyName, propertyValue);
        }

        @Override
        public void after() {
            super.after();
        }

        @Override
        public void before() throws Throwable {
            super.before();
        }
    }

    public static final String PROPERTY_NAME = "propertyName";
    public static final String PROPERTY_VALUE = "value";

    @Rule
    public SystemProperty systemProperty = new SystemProperty(PROPERTY_NAME, PROPERTY_VALUE);

    @Test
    public void testPropertySetAsRule() throws Exception {
        assertEquals(systemProperty.getPropertyValue(), System.getProperty(systemProperty.getPropertyName()));

    }

    @Test
    public void testKeepOriginalValue() throws Throwable {

        final String anotherValue = "anotherValue";
        final SystemPropertyWithPublicBeforeAndAfter systemPropertyRule = new SystemPropertyWithPublicBeforeAndAfter(
                PROPERTY_NAME,
                anotherValue);
        assertEquals(systemProperty.getPropertyValue(), System.getProperty(PROPERTY_NAME));

        systemPropertyRule.before();
        assertEquals(anotherValue, System.getProperty(PROPERTY_NAME));
        systemPropertyRule.after();

        assertEquals(systemPropertyRule.getOriginalPropertyValue(), System.getProperty(PROPERTY_NAME));
    }

}
