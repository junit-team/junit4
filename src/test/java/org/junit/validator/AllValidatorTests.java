package org.junit.validator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AnnotationsValidatorTest.class,
        AnnotationValidatorFactoryTest.class,
        PublicClassValidatorTest.class
})
public class AllValidatorTests {
}
