## Summary of Changes in version 4.10 ##

Thanks to a full cast of contributors of bug fixes and new features.

A full summary of commits between 4.9 and 4.10 is on [github](https://github.com/junit-team/junit/compare/r4.9...4.10)

### junit-dep has correct contents ###

junit-dep-4.9.jar incorrectly contained hamcrest classes, which could lead to version conflicts in projects that depend on hamcrest directly.  This is fixed in 4.10 [@dsaff, closing gh-309]

### RuleChain ###

The RuleChain rule allows ordering of TestRules:

```java
public static class UseRuleChain {
	@Rule
	public TestRule chain= RuleChain
	                       .outerRule(new LoggingRule("outer rule")
	                       .around(new LoggingRule("middle rule")
	                       .around(new LoggingRule("inner rule");

	@Test
	public void example() {
		assertTrue(true);
	}
}
```

writes the log

    starting outer rule
    starting middle rule
    starting inner rule
    finished inner rule
    finished middle rule
    finished outer rule

### TemporaryFolder ###

- `TemporaryFolder#newFolder(String... folderNames)` creates recursively deep temporary folders 
  [@rodolfoliviero, closing gh-283]
- `TemporaryFolder#newFile()` creates a randomly named new file, and `#newFolder()` creates a randomly named new folder
  [@Daniel Rothmaler, closing gh-299]

### Theories ###

The `Theories` runner does not anticipate theory parameters that have generic
types, as reported by github#64.  Fixing this won't happen until `Theories` is
moved to junit-contrib. In anticipation of this, 4.9.1 adds some of the
necessary machinery to the runner classes, and deprecates a method that only
the `Theories` runner uses, `FrameworkMethod`#producesType(). 
The Common Public License that JUnit is released under is now included
in the source repository.

Thanks to `@pholser` for identifying a potential resolution for github#64
and initiating work on it.

### Bug fixes ###

- Built-in Rules implementations
  - TemporaryFolder should not create files in the current working directory if applying the rule fails 
    [@orfjackal, fixing gh-278]
  - TestWatcher and TestWatchman should not call failed for AssumptionViolatedExceptions
    [@stefanbirkner, fixing gh-296]
- Javadoc bugs
  - Assert documentation [@stefanbirkner, fixing gh-134]
  - ClassRule [@stefanbirkner, fixing gh-254]
  - Parameterized  [@stefanbirkner, fixing gh-89]
  - Parameterized, again [@orfjackal, fixing gh-285]
- Miscellaneous
  - Useless code in RunAfters [@stefanbirkner, fixing gh-289]
  - Parameterized test classes should be able to have `@Category` annotations
    [@dsaff, fixing gh-291]
  - Error count should be initialized in junit.tests.framework.TestListenerTest [@stefanbirkner, fixing gh-225]
  - AssertionFailedError constructor shouldn't call super with null message [@stefanbirkner, fixing gh-318]
  - Clearer error message for non-static inner test classes  [@stefanbirkner, fixing gh-42]

### Minor changes ###

- Description, Result and Failure are Serializable [@ephox-rob, closing gh-101]
- FailOnTimeout is reusable, allowing for retrying Rules [@stefanbirkner, closing gh-265]
- New `ErrorCollector.checkThat` overload, that allows you to specify a reason [@drothmaler, closing gh-300]



