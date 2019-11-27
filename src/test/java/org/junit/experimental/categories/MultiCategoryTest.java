package org.junit.experimental.categories;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author tibor17
 * @version 4.12
 * @since 4.12
 */
public final class MultiCategoryTest {
    public interface A {}
    public interface B {}
    public interface C {}

    /**
     * This test is mentioned in {@code Categories} and any changes
     * must be reflected.
     */
    @Test
    public void runSuite() {
        // Targeting Test:
        Result testResult= JUnitCore.runClasses(MultiCategorySuite.class);

        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(2)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory({A.class, B.class})
    @Categories.ExcludeCategory(C.class)
    @Suite.SuiteClasses({CategoriesTest.class})
    public static final class MultiCategorySuite {}

    public static final class CategoriesTest {

        @Test
        @Category(A.class)
        public void a() {}

        @Test
        @Category(B.class)
        public void b() {}

        @Test
        @Category(C.class)
        public void c() {
            fail();
        }

        @Test
        public void anything() {
            fail();
        }
    }

    @Test
    public void inheritanceAnyIncluded() {
        Result testResult= JUnitCore.runClasses(InheritanceAny.class);
        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(3)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @Test
    public void inheritanceAllIncluded() {
        Result testResult= JUnitCore.runClasses(InheritanceAll.class);
        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(1)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @Test
    public void inheritanceAnyAll() {//any included, all excluded
        Result testResult= JUnitCore.runClasses(InheritanceAnyAll.class);
        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(3)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @Test
    public void inheritanceAllAny() {//all included, any excluded
        Result testResult= JUnitCore.runClasses(InheritanceAllAny.class);
        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(1)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(1)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
        assertFalse(testResult.wasSuccessful());
    }

    public static class X implements A {}
    public static class Y implements B {}
    public static class Z implements A, B {}
    public static class W implements A, B, C {}
    public static class Q implements A, C {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory({A.class, B.class})
    @Categories.ExcludeCategory(C.class)
    @Suite.SuiteClasses({InheritanceAnyTest.class})
    public static final class InheritanceAny {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value= {A.class, B.class}, matchAny= false)
    @Categories.ExcludeCategory(C.class)
    @Suite.SuiteClasses({InheritanceAllTest.class})
    public static final class InheritanceAll {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory({A.class, B.class})
    @Categories.ExcludeCategory(value= {A.class, C.class}, matchAny= false)
    @Suite.SuiteClasses({InheritanceAnyAllTest.class})
    public static final class InheritanceAnyAll {}

    @RunWith(Categories.class)
    @Categories.IncludeCategory(value= {A.class, B.class}, matchAny= false)
    @Categories.ExcludeCategory({A.class, C.class})
    @Suite.SuiteClasses({InheritanceAllAnyTest.class})
    public static final class InheritanceAllAny {}

    public static final class InheritanceAnyTest {
        @Test @Category(X.class) public void x() {}
        @Test @Category(Y.class) public void y() {}
        @Test @Category(Z.class) public void z() {}
        @Test @Category(W.class) public void w() { fail(); }
        @Test @Category(Q.class) public void q() { fail(); }
        @Test @Category(Runnable.class) public void runnable() { fail(); }
        @Test public void t() { fail(); }
    }

    public static final class InheritanceAllTest {
        @Test @Category(X.class) public void x() { fail(); }
        @Test @Category(Y.class) public void y() { fail(); }
        @Test @Category(Z.class) public void z() {}
        @Test @Category(W.class) public void w() { fail(); }
        @Test @Category(Q.class) public void q() { fail(); }
        @Test @Category(Runnable.class) public void runnable() { fail(); }
        @Test public void t() { fail(); }
    }

    public static final class InheritanceAnyAllTest {
        @Test @Category(X.class) public void x() {}
        @Test @Category(Y.class) public void y() {}
        @Test @Category(Z.class) public void z() {}
        @Test @Category(W.class) public void w() { fail(); }
        @Test @Category(Q.class) public void q() { fail(); }
        @Test @Category(Runnable.class) public void runnable() { fail(); }
        @Test public void t() { fail(); }
    }

    public static final class InheritanceAllAnyTest {
        @Test @Category(X.class) public void x() { fail(); }
        @Test @Category(Y.class) public void y() { fail(); }
        @Test @Category(Z.class) public void z() { fail(); }
        @Test @Category(W.class) public void w() { fail(); }
        @Test @Category(Q.class) public void q() { fail(); }
        @Test @Category(Runnable.class) public void runnable() { fail(); }
        @Test public void t() { fail(); }
    }
}
