package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ShufflableTest {
	public static class TestClassRunnerIsShufflable {
		private static String log= "";

		public static class ShuffleMe {
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

			@Test
			public void e() {
				log+= "e";
			}

		}

		@Before
		public void resetLog() {
			log= "";
		}

		@Test
		public void shuffledMethodWorksOnTestClassRunner() {
			Request randomly= Request.aClass(ShuffleMe.class).shuffled();

			new JUnitCore().run(randomly);
			String s= log;
			do {
				log= "";
				new JUnitCore().run(randomly);
			} while (s.equals(log));
			assertNotEquals(s, log);
		}

		@Test
		public void shuffledWithRandomMethodWorksOnTestClassRunner() {
			Random random1= new Random(0L);
			Random random2= new Random(1L);
			Request randomly1= Request.aClass(ShuffleMe.class).shuffled(
					random1);
			Request randomly2= Request.aClass(ShuffleMe.class).shuffled(
					random2);

			new JUnitCore().run(randomly1);
			assertEquals("ecbda", log);
			log= "";
			new JUnitCore().run(randomly2);
			assertEquals("cdbea", log);
		}

		@RunWith(Enclosed.class)
		public static class Enclosing {
			public static class A {
				@Test
				public void a() {
					log+= "Aa";
				}

				@Test
				public void b() {
					log+= "Ab";
				}

				@Test
				public void c() {
					log+= "Ac";
				}

				@Test
				public void d() {
					log+= "Ad";
				}

				@Test
				public void e() {
					log+= "Ae";
				}
			}

			public static class B {
				@Test
				public void a() {
					log+= "Ba";
				}

				@Test
				public void b() {
					log+= "Bb";
				}

				@Test
				public void c() {
					log+= "Bc";
				}

				@Test
				public void d() {
					log+= "Bd";
				}

				@Test
				public void e() {
					log+= "Be";
				}
			}

			public static class C {
				@Test
				public void a() {
					log+= "Ca";
				}

				@Test
				public void b() {
					log+= "Cb";
				}

				@Test
				public void c() {
					log+= "Cc";
				}

				@Test
				public void d() {
					log+= "Cd";
				}

				@Test
				public void e() {
					log+= "Ce";
				}
			}

			public static class D {
				@Test
				public void a() {
					log+= "Da";
				}

				@Test
				public void b() {
					log+= "Db";
				}

				@Test
				public void c() {
					log+= "Dc";
				}

				@Test
				public void d() {
					log+= "Dd";
				}

				@Test
				public void e() {
					log+= "De";
				}
			}

			public static class E {
				@Test
				public void a() {
					log+= "Ea";
				}

				@Test
				public void b() {
					log+= "Eb";
				}

				@Test
				public void c() {
					log+= "Ec";
				}

				@Test
				public void d() {
					log+= "Ed";
				}

				@Test
				public void e() {
					log+= "Ee";
				}
			}
		}

		@Test
		public void shuffledMethodWorksOnSuite() {
			Request randomly= Request.aClass(Enclosing.class).shuffled();

			new JUnitCore().run(randomly);
			String s= log;
			do {
				log= "";
				new JUnitCore().run(randomly);
			} while (s.equals(log));
			assertNotEquals(s, log);
		}

		@Test
		public void shuffledWithRandomMethodWorksOnSuite() {
			Random random1= new Random(0L);
			Random random2= new Random(1L);
			Request randomly1= Request.aClass(Enclosing.class).shuffled(
					random1);
			Request randomly2= Request.aClass(Enclosing.class).shuffled(
					random2);

			new JUnitCore().run(randomly1);
			assertEquals("EbEdEcEaEeAeAcAbAdAaCbCaCcCdCeBdBeBcBbBaDaDbDeDcDd",
					log);
			log= "";
			new JUnitCore().run(randomly2);
			assertEquals("CeCaCbCcCdAcAdAbAeAaEbEeEaEdEcDeDbDdDaDcBdBbBcBaBe",
					log);
		}
	}

	public static class TestClassRunnerIsShufflableWithSuiteMethod {
		private static String log= "";

		public static class ShuffleMe {
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

			@Test
			public void e() {
				log+= "e";
			}

			public static junit.framework.Test suite() {
				return new JUnit4TestAdapter(ShuffleMe.class);
			}
		}

		@Before
		public void resetLog() {
			log= "";
		}

		@Test
		public void shuffledMethodWorksOnTestClassRunner() {
			Request randomly= Request.aClass(ShuffleMe.class).shuffled();

			new JUnitCore().run(randomly);
			String s= log;
			do {
				log= "";
				new JUnitCore().run(randomly);
			} while (s.equals(log));
			assertNotEquals(s, log);
		}

		@Test
		public void shuffledWithRandomMethodWorksOnTestClassRunner() {
			Random random1= new Random(0L);
			Random random2= new Random(1L);
			Request randomly1= Request.aClass(ShuffleMe.class).shuffled(
					random1);
			Request randomly2= Request.aClass(ShuffleMe.class).shuffled(
					random2);

			new JUnitCore().run(randomly1);
			assertEquals("ecbda", log);
			log= "";
			new JUnitCore().run(randomly2);
			assertEquals("cdbea", log);
		}
	}
}
