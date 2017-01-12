package org.junit.tests.running.methods;

import org.junit.After;
import org.junit.Before;

class SuperShadowing {

    @Before
    public void before() {
        AnnotationTest.log += "Before super ";
    }

    @After
    public void after() {
        AnnotationTest.log += "After super ";
    }
}
