package org.junit.tests.experimental.rules;

import junit.tests.framework.AssertTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DeferredRule;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DeferredRuleTest {
    
    private int counter=0;

    private final TestRule around= new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            assertThat(counter,is(0));
            counter++;
        }

        @Override
        protected void after() {
            assertThat(counter,is(5));
            counter++;
        }

    };

    private final TestRule inner= new DeferredRule<TestRule>() {

        @Override
        protected TestRule build() {
            assertThat(counter,is(1));
            counter++;
            return new ExternalResource() {

                @Override
                protected void before() throws Throwable {
                    assertThat(counter,is(2));
                    counter++;
                }

                @Override
                protected void after() {
                    assertThat(counter,is(4));
                    counter++;
                }
            };
        }
    };

    @Rule
    public TestRule alls= RuleChain.outerRule(around).around(inner);

    @Test
    public void testSequence() {
        assertThat(counter,is(3));
        counter++;
    }
}