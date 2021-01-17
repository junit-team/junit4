## Summary of changes in version 4.13.2

# Rules

### [Pull request #1687:](https://github.com/junit-team/junit/pull/1687) Mark ThreadGroups created by FailOnTimeout as daemon groups

In JUnit 4.13 ([pull request #1517](https://github.com/junit-team/junit4/pull/1517)) an attempt was
made to fix leakage of the `ThreadGroup` instances created when a test is run with a timeout. That
change explicitly destroyed the `ThreadGroup` that was created for the time-limited test. Numerous
people reported problems that were caused by explicitly destroying the `ThreadGroup`.

In this change, the code was updated to call  `ThreadGroup.setDaemon(true)` instead of destroying the
ThreadGroup.

### [Pull request $1691:](https://github.com/junit-team/junit/pull/1691) Only create ThreadGroups if FailOnTimeout.lookForStuckThread is true.

In JUnit 4.12 ([pull request #742](https://github.com/junit-team/junit4/pull/742)) the `Timeout`
Rule was updated to optionally display the stacktrace of the thread that appears to be stuck
(enabled on an opt-in basis by passing `true` to `Timeout.Builder.lookForStuckThread(boolean)`).
When that change was made, time-limited tests were changed to start the new thread in a new
`ThreadGroup`, even if the test did not call `lookForStuckThread()`. This subtle change in
behavior resulted in visible behavior changes to some tests (for example, tests of code that uses
`java.beans.ThreadGroupContext`).

In this change, the code is updated to only create a new `ThreadGroup` if the caller calls
`Timeout.Builder.lookForStuckThread(true)`. Tests with timeouts that do not make this call will
behave as they did in JUnit 4.11 (and more similar to tests that do not have a timeout). This
unfortunately could result in visible changes of tests written or updated since the 4.12
release. If this change adversely affects your tests, you can create the `Timeout` rule via the
builder and call `Timeout.Builder.lookForStuckThread(true)`.

# Exceptions

### [Pull request #1654:](https://github.com/junit-team/junit/pull/1654) Fix for issue #1192: NotSerializableException with AssumptionViolatedException

This change fixes an issue where `AssumptionViolatedException` instances could not be serialized
if they were created with a constructor that takes in an `org.hamcrest.Matcher` instance (these
constructors are used if you use one of the `assumeThat()` methods in `org.junit.Assume`).
