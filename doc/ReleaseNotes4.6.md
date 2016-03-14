## Summary of Changes in version 4.6 ##

### Max ###

JUnit now includes a new experimental Core, `MaxCore`.  `MaxCore`
remembers the results of previous test runs in order to run new
tests out of order.  `MaxCore` prefers new tests to old tests, fast
tests to slow tests, and recently failing tests to tests that last
failed long ago.  There's currently not a standard UI for running
`MaxCore` included in JUnit, but there is a UI included in the JUnit
Max Eclipse plug-in at:

  http://www.junitmax.com/junitmax/subscribe.html

Example:

```java
public static class TwoUnEqualTests {
	@Test
	public void slow() throws InterruptedException {
		Thread.sleep(100);
		fail();
	}

	@Test
	public void fast() {
		fail();
	}
}

@Test
public void rememberOldRuns() {
	File maxFile = new File("history.max");
	MaxCore firstMax = MaxCore.storedLocally(maxFile);
	firstMax.run(TwoUnEqualTests.class);

	MaxCore useHistory= MaxCore.storedLocally(maxFile);
	List<Failure> failures= useHistory.run(TwoUnEqualTests.class)
		.getFailures();
	assertEquals("fast", failures.get(0).getDescription().getMethodName());
	assertEquals("slow", failures.get(1).getDescription().getMethodName());
}
```

### Test scheduling strategies ###

`JUnitCore` now includes an experimental method that allows you to
specify a model of the `Computer` that runs your tests.  Currently,
the only built-in Computers are the default, serial runner, and two
runners provided in the `ParallelRunner` class:
`ParallelRunner.classes()`, which runs classes in parallel, and
`ParallelRunner.methods()`, which runs classes and methods in parallel.

This feature is currently less stable than MaxCore, and may be
merged with MaxCore in some way in the future.

Example:

```java
public static class Example {
	@Test public void one() throws InterruptedException {
		Thread.sleep(1000);
	}
	@Test public void two() throws InterruptedException {
		Thread.sleep(1000);
	}
}
	
@Test public void testsRunInParallel() {
	long start= System.currentTimeMillis();
	Result result= JUnitCore.runClasses(ParallelComputer.methods(),
			Example.class);
	assertTrue(result.wasSuccessful());
	long end= System.currentTimeMillis();
	assertThat(end - start, betweenInclusive(1000, 1500));
}
```

### Comparing double arrays ###

Arrays of doubles can be compared, using a delta allowance for equality:

```java
@Test
public void doubleArraysAreEqual() {
	assertArrayEquals(new double[] {1.0, 2.0}, new double[] {1.0, 2.0}, 0.01);
}
```
	
### `Filter.matchDescription` API ###

Since 4.0, it has been possible to run a single method using the `Request.method` 
API.  In 4.6, the filter that implements this is exposed as `Filter.matchDescription`.

### Documentation ###

- A couple classes and packages that once had empty javadoc have been
  doc'ed.
  
- Added how to run JUnit from the command line to the cookbook.

- junit-4.x.zip now contains build.xml

### Bug fixes ###
- Fixed overly permissive @DataPoint processing (2191102)
- Fixed bug in test counting after an ignored method (2106324)
