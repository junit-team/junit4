package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

@RunWith(Enclosed.class)
public class SortableTest {
    private static Comparator<Description> forward() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        };
    }

    private static Comparator<Description> backward() {
        return new Comparator<Description>() {
            public int compare(Description o1, Description o2) {
                return o2.getDisplayName().compareTo(o1.getDisplayName());
            }
        };
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
        public void sortingRandomlyWorksOnTestClassRunner() {
            Request randomly = Request.aClass(SortMe.class).sortWith(Sorter.RANDOM);

            new JUnitCore().run(randomly);
            String s = log;
            do {
            	new JUnitCore().run(randomly);
            } while (s.equals(log));
            assertNotEquals(s, log);
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
        
        @Test
        public void sortingRandomlyWorksOnSuite() {
            Request randomly = Request.aClass(Enclosing.class).sortWith(Sorter.RANDOM);

            new JUnitCore().run(randomly);
            String s = log;
            do {
            	new JUnitCore().run(randomly);
            } while (s.equals(log));
            assertNotEquals(s, log);
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

        @Test
        public void sortingRandomlyWorksOnTestClassRunner() {
            Request randomly = Request.aClass(SortMe.class).sortWith(Sorter.RANDOM);

            new JUnitCore().run(randomly);
            String s = log;
            do {
            	new JUnitCore().run(randomly);
            } while (s.equals(log));
            assertNotEquals(s, log);
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
    
	/*
	 * Class used to test the random sorter using a Chi-Square test. Note that
	 * most values in this class are dependent on the number of Test methods to
	 * be randomly sorted.
	 */
	public static class TestClassRunnerRandomSort {
		private static String log= "";

		/*
		 * Class with four Test methods to be randomly sorted. This will yield
		 * 4! possible permutations, all of which should be equally likely to
		 * occur.
		 */
		public static class RandomSortMe {
			@Test
			public void a() {
				log+= "a";
			}

			@Test
			public void b() {
				log+= "b";
			}

			@Test
			public void c() {
				log+= "c";
			}

			@Test
			public void d() {
				log+= "d";
			}
		}

		/* Expected probability that any permutation will occur. This is 1/(4!). */
		private final double EXPECTED_PROBABILITY= 1.0d / 24.0d;

		/*
		 * Default number of iterations to use. This should be a sufficiently
		 * large number. Here the number of iterations chosen was (17.36 / (1/24)
		 * * 24), that is (<desired occurrences per observation> / <probability>
		 * * <permutations possible>)
		 */
		private final long ITERATIONS= 10000;

		/*
		 * Default number of chi-square tests to run. This should be an odd
		 * number.
		 */
		private final int CHI_SQUARE_TESTS= 9;

		/* Function to give an initial hashtable used in the random sort tests. */
		private Hashtable<String, Long> getInitialHashtable() {
			Hashtable<String, Long> hashtable= new Hashtable<String, Long>();
			/*
			 * Set the hashtable keys with all permutations possible from
			 * sorting the SortMe class. TODO: Generate the permutations using
			 * some algorithm.
			 */
			hashtable.put("abcd", 0L);
			hashtable.put("abdc", 0L);
			hashtable.put("acbd", 0L);
			hashtable.put("acdb", 0L);
			hashtable.put("adbc", 0L);
			hashtable.put("adcb", 0L);
			hashtable.put("bacd", 0L);
			hashtable.put("badc", 0L);
			hashtable.put("bcad", 0L);
			hashtable.put("bcda", 0L);
			hashtable.put("bdac", 0L);
			hashtable.put("bdca", 0L);
			hashtable.put("cabd", 0L);
			hashtable.put("cadb", 0L);
			hashtable.put("cbad", 0L);
			hashtable.put("cbda", 0L);
			hashtable.put("cdab", 0L);
			hashtable.put("cdba", 0L);
			hashtable.put("dabc", 0L);
			hashtable.put("dacb", 0L);
			hashtable.put("dbac", 0L);
			hashtable.put("dbca", 0L);
			hashtable.put("dcab", 0L);
			hashtable.put("dcba", 0L);
			return hashtable;
		}

		/*
		 * Chi-Square critical values. The values here represent alpha = 0.25d.
		 */
		private final double CHI_SQUARE_CV_ALPHA= 27.1413360d;

		private final double CHI_SQUARE_CV_ONE_MINUS_ALPHA= 18.13729674d;

		/* Function performing the chi-square test. */
		private boolean chiSquareTest(Hashtable<String, Long> hashtable) {
			double V= 0.0d;
			Enumeration<String> keys= hashtable.keys();
			while (keys.hasMoreElements()) {
				long value= hashtable.get(keys.nextElement());
				V+= StrictMath.pow(value, 2) / EXPECTED_PROBABILITY;
			}
			V/= ITERATIONS;
			V-= ITERATIONS;
			if (CHI_SQUARE_CV_ONE_MINUS_ALPHA <= V && V <= CHI_SQUARE_CV_ALPHA) {
				return true;
			}
			return false;
		}

		private boolean randomSortTest(Request request) {
			double passes= 0.0d, failures= 0.0d;

			/*
			 * Perform TESTS number of chi-square tests. NOTE: Each test is
			 * suppose to use a different data set. Here however, the same data
			 * set is used for all tests.
			 */
			for (int i= 0; i < CHI_SQUARE_TESTS; i++) {
				/*
				 * Randomly sort the tests ITERATIONS number of times and keep
				 * count how many times each permutation occurs.
				 */
				Hashtable<String, Long> hashtable= getInitialHashtable();
				for (int j= 0; j < ITERATIONS; j++) {
					log= "";
					new JUnitCore().run(request);
					hashtable.put(log, hashtable.get(log) + 1);
				}
				if (chiSquareTest(hashtable)) {
					passes+= 1.0d;
				} else {
					failures+= 1.0d;
				}
			}

			return (passes / failures > 0.5d) ? true : false;
		}

		@Test
		public void randomSortGoodRandomSorterTest() {
			Request randomly= Request.aClass(RandomSortMe.class).sortWith(
					Sorter.RANDOM);
			assertTrue(randomSortTest(randomly));
		}

		/* A Comparator that returns -1 or 1 with equal probability */
		private static Comparator<Description> random() {
			return new Comparator<Description>() {
				public int compare(Description o1, Description o2) {
					return StrictMath.random() >= 0.5d ? 1 : -1;
				}
			};
		}

		@Test
		public void randomSortBadRandomSorterTest() {
			Request randomly= Request.aClass(RandomSortMe.class).sortWith(
					random());
			assertFalse(randomSortTest(randomly));
		}
	}
}
