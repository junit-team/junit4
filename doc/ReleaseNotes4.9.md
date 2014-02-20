## Summary of Changes in version 4.9, final ##

Release theme: Test-class and suite level Rules.

### ClassRule ###

The `ClassRule` annotation extends the idea of method-level Rules,
adding static fields that can affect the operation of a whole class.  Any
subclass of `ParentRunner`, including the standard `BlockJUnit4ClassRunner` 
and `Suite` classes, will support `ClassRule`s.

For example, here is a test suite that connects to a server once before
all the test classes run, and disconnects after they are finished:

```java
@RunWith(Suite.class)
@SuiteClasses({A.class, B.class, C.class})
public class UsesExternalResource {
	public static Server myServer= new Server();

	@ClassRule
	public static ExternalResource resource= new ExternalResource() {
		@Override
		protected void before() throws Throwable {
			myServer.connect();
		};
	
		@Override
		protected void after() {
			myServer.disconnect();
		};
	};
}
```

### TestRule ###

In JUnit 4.9, fields that can be annotated with either `@Rule` or `@ClassRule`
should be of type `TestRule`.  The old `MethodRule` type, which only made sense
for method-level rules, will still work, but is deprecated.

Most built-in Rules have been moved to the new type already, in a way that
should be transparent to most users.  `TestWatchman` has been deprecated,
and replaced by `TestWatcher`, which has the same functionality, but implements
the new type.

### Maven support ###

Maven bundles have, in the past, been uploaded by kind volunteers.  Starting
with this release, the JUnit team is attempting to perform this task ourselves.

### LICENSE checked in ###

The Common Public License that JUnit is released under is now included
in the source repository.

### Bug fixes ###

- github#98: assumeTrue() does not work with expected exceptions
- github#74: Categories + Parameterized
  
  In JUnit 4.8.2, the Categories runner would fail to run correctly
  if any contained test class had a custom Runner with a structure
  significantly different from the built-in Runner.  With this fix,
  such classes can be assigned one or more categories at the class level,
  and will be run correctly.  Trying to assign categories to methods within
  such a class will flag an error.

- github#38: ParentRunner filters more than once

  Thanks to `@reinholdfuereder`

- github#248: protected BlockJUnit4ClassRunner#rules method removed from 4.8.2
- github#187: Accidental dependency on Java 6

Thanks to `@kcooney` for:

- github#163: Bad comparison failure message when using assertEquals(String, String)
- github#227: ParentRunner now assumes that getChildren() returns a modifiable list

### Minor changes ###

- Backed out unused folder "experimental-use-of-antunit", replaced by 
  bash-based script at build_tests.sh
- Various Javadoc fixes

Thanks to `@kcooney` for:

- Made MultipleFailureException public, to assist extension writers.
- github#240: Add "test" target to build.xml, for faster ant-driven testing.
- github#247: Give InitializationError a useful message
