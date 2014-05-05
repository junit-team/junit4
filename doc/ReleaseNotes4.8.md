## Summary of Changes in version 4.8 ##

### Categories ###

From a given set of test classes, the `Categories` runner
runs only the classes and methods
that are annotated with either the category given with the `@IncludeCategory`
annotation, or a subtype of that category. Either classes or interfaces can be
used as categories. Subtyping works, so if you say `@IncludeCategory(SuperClass.class)`,
a test marked `@Category({SubClass.class})` will be run.

You can also exclude categories by using the `@ExcludeCategory` annotation
 
Example:

```java
public interface FastTests { /* category marker */ }
public interface SlowTests { /* category marker */ }

public class A {
	@Test
	public void a() {
		fail();
	}

	@Category(SlowTests.class)
	@Test
	public void b() {
	}
}

@Category({SlowTests.class, FastTests.class})
public class B {
	@Test
	public void c() {
	}
}

@RunWith(Categories.class)
@IncludeCategory(SlowTests.class)
@SuiteClasses( { A.class, B.class }) // Note that Categories is a kind of Suite
public class SlowTestSuite {
	// Will run A.b and B.c, but not A.a
}

@RunWith(Categories.class)
@IncludeCategory(SlowTests.class)
@ExcludeCategory(FastTests.class)
@SuiteClasses( { A.class, B.class }) // Note that Categories is a kind of Suite
public class SlowTestSuite {
	// Will run A.b, but not A.a or B.c
}
```

### Bug fixes ###

- github#16: thread safety of Result counting
