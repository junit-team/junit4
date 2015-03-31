package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Orderable;
import org.junit.runner.manipulation.Ordering;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(Enclosed.class)
public class OrderableTest {
    private static Comparator<Description> forward() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        };
    }

    private static Comparator<Description> reverse() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o2.getDisplayName().compareTo(o1.getDisplayName());
            }
        };
    }
 
    private static Ordering orderedAlphanumerically() {
        return Ordering.sortedBy(forward());
    }

    private static Ordering orderedReverseAlphanumerically() {
        return Ordering.sortedBy(reverse());
    }

    public static class TestClassRunnerIsOrderable {
        private static String log = "";

        public static class OrderMe {
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
        public void orderingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(OrderMe.class).orderWith(orderedAlphanumerically());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void orderingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(OrderMe.class).orderWith(orderedReverseAlphanumerically());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
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
        public void orderingForwardWorksOnSuite() {
            Request forward = Request.aClass(Enclosing.class).orderWith(orderedAlphanumerically());

            new JUnitCore().run(forward);
            assertEquals("AaAbAcBaBbBc", log);
        }

        @Test
        public void orderingBackwardWorksOnSuite() {
            Request backward = Request.aClass(Enclosing.class).orderWith(orderedReverseAlphanumerically());

            new JUnitCore().run(backward);
            assertEquals("BcBbBaAcAbAa", log);
        }
    }

    public static class TestClassRunnerIsSortableViaOrderable {
        private static String log = "";

        /**
         * A Runner that implements {@link Orderable} but not {@link Sortable}.
         */
        public static class OrderableRunner extends Runner implements Orderable {
            private final BlockJUnit4ClassRunner delegate;

            public OrderableRunner(Class<?> klass) throws Throwable {
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

            public void order(Ordering ordering) {
                delegate.order(ordering);
            }
        }

        @RunWith(OrderableRunner.class)
        public static class OrderMe {
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
                return new JUnit4TestAdapter(OrderMe.class);
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void sortingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(OrderMe.class).sortWith(forward());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void sortingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(OrderMe.class).sortWith(reverse());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }
    }

    public static class TestClassRunnerIsOrderableWithSuiteMethod {
        private static String log = "";

        public static class OrderMe {
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
                return new JUnit4TestAdapter(OrderMe.class);
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void orderingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(OrderMe.class).orderWith(orderedAlphanumerically());

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void orderingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(OrderMe.class).orderWith(orderedReverseAlphanumerically());

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }
    }

    public static class UnOrderableRunnersAreHandledWithoutCrashing {
        public static class UnOrderableRunner extends Runner {
            public UnOrderableRunner(Class<?> klass) {
            }

            @Override
            public Description getDescription() {
                return Description.EMPTY;
            }

            @Override
            public void run(RunNotifier notifier) {
            }
        }

        @RunWith(UnOrderableRunner.class)
        public static class UnOrderable {
            @Test
            public void a() {
            }
        }

        @Test
        public void unOrderablesAreHandledWithoutCrashing() {
            Request unordered = Request.aClass(UnOrderable.class).orderWith(orderedAlphanumerically());
            new JUnitCore().run(unordered);
        }
    }
}
