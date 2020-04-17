package org.junit.tests.manipulation;

import static java.util.Collections.reverseOrder;
import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Orderable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.MethodSorters;

@RunWith(Enclosed.class)
public class SortableTest {
    private static Comparator<Description> forward() {
        return Comparators.alphanumeric();
    }

    private static Comparator<Description> backward() {
        return reverseOrder(Comparators.alphanumeric());
    }

    public static class TestClassRunnerIsSortable {
        private static String log = "";

        public static class SortMe {
            @Test
            public void a() {
                log += "a";
            }

            @Test
            public void b() {
                log += "b";
            }

            @Test
            public void c() {
                log += "c";
            }
        }

        @FixMethodOrder(MethodSorters.NAME_ASCENDING)
        public static class DoNotSortMe {
            @Test
            public void a() {
                log += "a";
            }

            @Test
            public void b() {
                log += "b";
            }

            @Test
            public void c() {
                log += "c";
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void sortingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(SortMe.class).sortWith(forward());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void sortingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(SortMe.class).sortWith(backward());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }

        @Test
        public void sortingBackwardDoesNothingOnTestClassRunnerWithFixMethodOrder() {
            Request backward = Request.aClass(DoNotSortMe.class).sortWith(backward());

            new JUnitCore().run(backward);
            assertEquals("abc", log);
        }

        @RunWith(Enclosed.class)
        public static class Enclosing {
            public static class A {
                @Test
                public void a() {
                    log += "Aa";
                }

                @Test
                public void b() {
                    log += "Ab";
                }

                @Test
                public void c() {
                    log += "Ac";
                }
            }

            public static class B {
                @Test
                public void a() {
                    log += "Ba";
                }

                @Test
                public void b() {
                    log += "Bb";
                }

                @Test
                public void c() {
                    log += "Bc";
                }
            }
        }

        @Test
        public void sortingForwardWorksOnSuite() {
            Request forward = Request.aClass(Enclosing.class).sortWith(forward());

            new JUnitCore().run(forward);
            assertEquals("AaAbAcBaBbBc", log);
        }

        @Test
        public void sortingBackwardWorksOnSuite() {
            Request backward = Request.aClass(Enclosing.class).sortWith(backward());

            new JUnitCore().run(backward);
            assertEquals("BcBbBaAcAbAa", log);
        }
    }

    public static class TestClassRunnerIsSortableWithSuiteMethod {
        private static String log = "";

        public static class SortMe {
            @Test
            public void a() {
                log += "a";
            }

            @Test
            public void b() {
                log += "b";
            }

            @Test
            public void c() {
                log += "c";
            }

            public static junit.framework.Test suite() {
                return new JUnit4TestAdapter(SortMe.class);
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void sortingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(SortMe.class).sortWith(forward());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void sortingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(SortMe.class).sortWith(backward());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }
    }

    public static class UnsortableRunnersAreHandledWithoutCrashing {
        public static class UnsortableRunner extends Runner {
            public UnsortableRunner(Class<?> klass) {
            }

            @Override
            public Description getDescription() {
                return Description.EMPTY;
            }

            @Override
            public void run(RunNotifier notifier) {
            }
        }

        @RunWith(UnsortableRunner.class)
        public static class Unsortable {
            @Test
            public void a() {
            }
        }

        @Test
        public void unsortablesAreHandledWithoutCrashing() {
            Request unsorted = Request.aClass(Unsortable.class).sortWith(forward());
            new JUnitCore().run(unsorted);
        }
    }

    public static class TestOnlySortableClassRunnerIsSortable {
        private static String log = "";

        /**
         * A Runner that implements {@link Sortable} but not {@link Orderable}.
         */
        public static class SortableRunner extends Runner implements Sortable {
            private final BlockJUnit4ClassRunner delegate;

            public SortableRunner(Class<?> klass) throws Throwable {
                delegate = new BlockJUnit4ClassRunner(klass);
            }

            @Override
            public void run(RunNotifier notifier) {
                delegate.run(notifier);
            }

            @Override
            public Description getDescription() {
                return delegate.getDescription();
            }

            public void sort(Sorter sorter) {
                delegate.sort(sorter);
            }
        }

        @RunWith(SortableRunner.class)
        public static class SortMe {
            @Test
            public void a() {
                log += "a";
            }

            @Test
            public void b() {
                log += "b";
            }

            @Test
            public void c() {
                log += "c";
            }

            public static junit.framework.Test suite() {
                return new JUnit4TestAdapter(SortMe.class);
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void sortingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(SortMe.class).sortWith(forward());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void sortingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(SortMe.class).sortWith(backward());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }
    }
}
