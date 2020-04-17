package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.OrderWith;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Alphanumeric;
import org.junit.runner.notification.RunNotifier;

@RunWith(Enclosed.class)
public class OrderWithTest {
 
    public static class TestClassRunnerIsOrderableViaOrderWith {
        private static String log = "";

        public static class Unordered {
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

        @OrderWith(AlphanumericOrdering.class)
        public static class OrderedAlphanumerically extends Unordered {
        }

        @OrderWith(ReverseAlphanumericOrdering.class)
        public static class OrderedReverseAlphanumerically extends Unordered {
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void orderingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(OrderedAlphanumerically.class);

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void orderingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(OrderedReverseAlphanumerically.class);

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }

        @RunWith(Enclosed.class)
        public static class UnorderedSuite {
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

        @OrderWith(AlphanumericOrdering.class)
        public static class SuiteOrderedAlphanumerically extends UnorderedSuite {
        }

        @OrderWith(ReverseAlphanumericOrdering.class)
        public static class SuiteOrderedReverseAlphanumerically extends UnorderedSuite {
        }

        @Test
        public void orderingForwardWorksOnSuite() {
            Request forward = Request.aClass(SuiteOrderedAlphanumerically.class);

            new JUnitCore().run(forward);
            assertEquals("AaAbAcBaBbBc", log);
        }

        @Test
        public void orderingBackwardWorksOnSuite() {
            Request backward = Request.aClass(SuiteOrderedReverseAlphanumerically.class);

            new JUnitCore().run(backward);
            assertEquals("BcBbBaAcAbAa", log);
        }
    }

    public static class TestClassRunnerIsSortableViaOrderWith {
        private static String log = "";

        public static class Unordered {
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

        @OrderWith(Alphanumeric.class)
        public static class SortedAlphanumerically extends Unordered {
        }

        @OrderWith(ReverseAlphanumericSorter.class)
        public static class SortedReverseAlphanumerically extends Unordered {
        }
 
        @Test
        public void sortingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(SortedAlphanumerically.class);

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void sortingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(SortedReverseAlphanumerically.class);

            new JUnitCore().run(backward);
            assertEquals("cba", log);
        }
    }

    public static class TestClassRunnerIsOrderableWithSuiteMethod {
        private static String log = "";

        public static class Unordered {
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
        
        @OrderWith(AlphanumericOrdering.class)
        public static class OrderedAlphanumerically extends Unordered {
 
            public static junit.framework.Test suite() {
                return new JUnit4TestAdapter(OrderedAlphanumerically.class);
            }
        }

        @OrderWith(ReverseAlphanumericOrdering.class)
        public static class OrderedReverseAlphanumerically extends Unordered {

            public static junit.framework.Test suite() {
                return new JUnit4TestAdapter(OrderedReverseAlphanumerically.class);
            }
        }

        @Before
        public void resetLog() {
            log = "";
        }

        @Test
        public void orderingForwardWorksOnTestClassRunner() {
            Request forward = Request.aClass(OrderedAlphanumerically.class);

            new JUnitCore().run(forward);
            assertEquals("abc", log);
        }

        @Test
        public void orderingBackwardWorksOnTestClassRunner() {
            Request backward = Request.aClass(OrderedReverseAlphanumerically.class);

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
            Request unordered = Request.aClass(UnOrderable.class).orderWith(
                    AlphanumericOrdering.INSTANCE);
            new JUnitCore().run(unordered);
        }
    }
}
