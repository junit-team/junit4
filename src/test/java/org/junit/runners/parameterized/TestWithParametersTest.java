package org.junit.runners.parameterized;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.rules.ExpectedException.none;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.TestClass;

public class TestWithParametersTest {
    private static final String DUMMY_NAME = "dummy name";

    private static final TestClass DUMMY_TEST_CLASS = new TestClass(
            DummyClass.class);

    private static final List<Object> DUMMY_PARAMETERS = Arrays
            .<Object> asList("a", "b");

    @Rule
    public final ExpectedException thrown = none();

    @Test
    public void cannotBeCreatedWithoutAName() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The name is missing.");
        new TestWithParameters(null, DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
    }

    @Test
    public void cannotBeCreatedWithoutTestClass() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The test class is missing.");
        new TestWithParameters(DUMMY_NAME, null, DUMMY_PARAMETERS);
    }

    @Test
    public void cannotBeCreatedWithoutParameters() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("The parameters are missing.");
        new TestWithParameters(DUMMY_NAME, DUMMY_TEST_CLASS,
                (List<Object>) null);
    }

    @Test
    public void doesNotAllowToModifyProvidedParameters() {
        TestWithParameters test = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        thrown.expect(UnsupportedOperationException.class);
        test.getParameters().set(0, "another parameter");
    }

    @Test
    public void doesNotConsiderParametersWhichChangedAfterTestInstantiation() {
        List<Object> parameters = Arrays.<Object> asList("dummy parameter");
        TestWithParameters test = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, parameters);
        parameters.set(0, "another parameter");
        assertEquals(asList("dummy parameter"), test.getParameters());
    }

    @Test
    public void isEqualToTestWithSameNameAndTestClassAndParameters() {
        TestWithParameters firstTest = new TestWithParameters(DUMMY_NAME,
                new TestClass(DummyClass.class), Arrays.<Object> asList("a",
                        "b"));
        TestWithParameters secondTest = new TestWithParameters(DUMMY_NAME,
                new TestClass(DummyClass.class), Arrays.<Object> asList("a",
                        "b"));
        assertEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentName() {
        TestWithParameters firstTest = new TestWithParameters("name",
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters("another name",
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentTestClass() {
        TestWithParameters firstTest = new TestWithParameters(DUMMY_NAME,
                new TestClass(DummyClass.class), DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters(DUMMY_NAME,
                new TestClass(AnotherDummyClass.class), DUMMY_PARAMETERS);
        assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToTestWithDifferentParameters() {
        TestWithParameters firstTest = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, Arrays.<Object> asList("a"));
        TestWithParameters secondTest = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, Arrays.<Object> asList("b"));
        assertNotEquals(firstTest, secondTest);
    }

    @Test
    public void isNotEqualToObjectWithDifferentClass() {
        TestWithParameters test = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        assertNotEquals(test, new Integer(3));
    }

    @Test
    public void hasSameHashCodeAsEqualTest() {
        TestWithParameters firstTest = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        TestWithParameters secondTest = new TestWithParameters(DUMMY_NAME,
                DUMMY_TEST_CLASS, DUMMY_PARAMETERS);
        assertEquals(firstTest.hashCode(), secondTest.hashCode());
    }

    @Test
    public void hasMeaningfulToString() {
        TestWithParameters test = new TestWithParameters("name", new TestClass(
                DummyClass.class), Arrays.<Object> asList("first parameter",
                "second parameter"));
        assertEquals(
                "Wrong toString().",
                "org.junit.runners.parameterized.TestWithParametersTest$DummyClass 'name' with parameters [first parameter, second parameter]",
                test.toString());
    }

    private static class DummyClass {
    }

    private static class AnotherDummyClass {
    }
}
