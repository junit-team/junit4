package org.junit.tests.experimental.categories;


import java.lang.reflect.Method;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.CategoryValidator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CategoryValidatorTest {

    public static class SampleCategory {
    }

    public static class CategoryTest {
        @BeforeClass
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndBeforeClass() {
        }

        @AfterClass
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndAfterClass() {
        }

        @Before
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndBefore() {
        }

        @After
        @Category(value = SampleCategory.class)
        public static void methodWithCategoryAndAfter() {
        }

        @Category(value = SampleCategory.class)
        public static void methodWithCategory() {
        }
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBeforeClass() throws NoSuchMethodException {
        Method method = CategoryTest.class.getMethod("methodWithCategoryAndBeforeClass");
        testAndAssertErrrorMessage(method, "@BeforeClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfterClass() throws NoSuchMethodException {
        Method method = CategoryTest.class.getMethod("methodWithCategoryAndAfterClass");
        testAndAssertErrrorMessage(method, "@AfterClass can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithBefore() throws NoSuchMethodException {
        Method method = CategoryTest.class.getMethod("methodWithCategoryAndBefore");
        testAndAssertErrrorMessage(method, "@Before can not be combined with @Category");
    }

    @Test
    public void errorIsAddedWhenCategoryIsUsedWithAfter() throws NoSuchMethodException {
        Method method = CategoryTest.class.getMethod("methodWithCategoryAndAfter");
        testAndAssertErrrorMessage(method, "@After can not be combined with @Category");
    }

    private void testAndAssertErrrorMessage(Method method, String expectedErrorMessage) throws NoSuchMethodException {
        List<Throwable> errors = new CategoryValidator().validateAnnotatedMethod(method);

        assertThat(errors.size(), is(1));
        Throwable throwable = errors.get(0);
        assertThat(throwable.getMessage(), is(expectedErrorMessage));
    }

    @Test
    public void errorIsNotAddedWhenCategoryIsNotCombinedWithIllegalCombination() throws NoSuchMethodException {
        Method beforeClass = CategoryTest.class.getMethod("methodWithCategory");
        List<Throwable> errors = new CategoryValidator().validateAnnotatedMethod(beforeClass);

        assertThat(errors.size(), is(0));
    }
}
