## Summary of Changes in version 4.7 ##

### Rules ###

- Rules allow very flexible addition or redefinition of the behavior
  of each test method in a test class.  Testers can reuse or extend one of the 
  provided Rules below, or write their own.
 
  For more on this feature, see http://www.threeriversinstitute.org/blog/?p=155
 
- The TemporaryFolder Rule allows creation of files and folders
  that are guaranteed to be deleted when the test method finishes
  (whether it passes or fails):
  
  	public static class HasTempFolder {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingTempFolder() throws IOException {
			File createdFile= folder.newFile("myfile.txt");
			File createdFolder= folder.newFolder("subfolder");
			// ...
		}
	}

- ExternalResource is a base class for Rules (like TemporaryFolder)
  that set up an external resource before a test (a file, socket, server,
  database connection, etc.), and guarantee to tear it down afterward:
  
  	public static class UsesExternalResource {
  		Server myServer = new Server();
  		
		@Rule public ExternalResource resource = new ExternalResource() {
			@Override
			protected void before() throws Throwable {
				myServer.connect();
			};
			
			@Override
			protected void after() {
				myServer.disconnect();
			};
		};
		
		@Test public void testFoo() {
			new Client().run(myServer);
		}
	}

- The ErrorCollector Rule allows execution of a test to continue
  after the first problem is found (for example, to collect _all_ the 
  incorrect rows in a table, and report them all at once):

	public static class UsesErrorCollectorTwice {
		@Rule
		public ErrorCollector collector= new ErrorCollector();
		
		@Test public void example() {
			collector.addError(new Throwable("first thing went wrong"));
			collector.addError(new Throwable("second thing went wrong"));
		}
	}
	
- Verifier is a base class for Rules like ErrorCollector, which
  can turn otherwise passing test methods into failing tests if a verification
  check is failed
  
    public static class ErrorLogVerifier() {
       private ErrorLog errorLog = new ErrorLog();
    
       @Rule
       public MethodRule verifier = new Verifier() {
          @Override public void verify() {
             assertTrue(errorLog.isEmpty());
          }
       }
       
       @Test public void testThatMightWriteErrorLog() {
          // ...
       }
    }

- TestWatchman is a base class for Rules that take note
  of the testing action, without modifying it.
  For example, this class will keep a log of each passing and failing 
  test:
  
	public static class WatchmanTest {
		private static String watchedLog;

		@Rule
		public MethodRule watchman= new TestWatchman() {
			@Override
			public void failed(Throwable e, FrameworkMethod method) {
				watchedLog+= method.getName() + " "
						+ e.getClass().getSimpleName() + "\n";
			}

			@Override
			public void succeeded(FrameworkMethod method) {
				watchedLog+= method.getName() + " " + "success!\n";
			}
		};

		@Test
		public void fails() {
			fail();
		}

		@Test
		public void succeeds() {
		}
	}

- The TestName Rule makes the current test name available inside test methods:

	public class NameRuleTest {
		@Rule public TestName name = new TestName();
		
		@Test public void testA() {
			assertEquals("testA", name.getMethodName());
		}
		
		@Test public void testB() {
			assertEquals("testB", name.getMethodName());
		}
	}

- The Timeout Rule applies the same timeout to all test methods in a class:

	public static class HasGlobalTimeout {
		public static String log;
		
		@Rule public MethodRule globalTimeout = new Timeout(20);
		
		@Test public void testInfiniteLoop1() {
			log+= "ran1";
			for(;;) {}
		}
		
		@Test public void testInfiniteLoop2() {
			log+= "ran2";
			for(;;) {}
		}
	}

- The ExpectedException Rule allows in-test specification
  of expected exception types and messages:
    
	public static class HasExpectedException {
		@Rule
		public ExpectedException thrown= ExpectedException.none();

		@Test
		public void throwsNothing() {

		}

		@Test
		public void throwsNullPointerException() {
			thrown.expect(NullPointerException.class);
			throw new NullPointerException();
		}

		@Test
		public void throwsNullPointerExceptionWithMessage() {
			thrown.expect(NullPointerException.class);
			thrown.expectMessage("happened?");
			thrown.expectMessage(startsWith("What"));
			throw new NullPointerException("What happened?");
		}
	}

### Timeouts ###
- Tests that time out now show the stack trace of the test thread.

### Matchers ###
- Due to typing incompatibilities, JUnit is still including the 1.1 release
  of hamcrest.  This is not a change from 4.6, but is a change from
  pre-beta releases of 4.7.  Due to this incompatibility, tests using
  Hamcrest 1.2 must still use the MatcherAssert.assertThat method from 
  Hamcrest, not Assert.assertThat from JUnit.
  
### Docs ###
- Javadocs now link to online JDK javadocs (bug 2090230)
- Parameterized runner javadocs improved (bug 2186792)
- Fixed Javadoc code sample for AfterClass (2126279)
- Fixed Javadoc for assertArraysEqual(float[], float[])

### Bug fixes ###
- Fixed: BaseTestRunner.getTest() requires class to extend TestCase (1812200)
- Fixed: Suite does not allow for inheritance in annotations (2783118)
- Fixed: ParallelComputer skipped tests that took longer than 2 seconds
